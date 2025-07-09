/* ******************************************************************************
 * Copyright (c) 2025 Calypso Networks Association https://calypsonet.org/
 *
 * See the NOTICE file(s) distributed with this work for additional information
 * regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the BSD 3-Clause License which is available at
 * https://opensource.org/licenses/BSD-3-Clause.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 ****************************************************************************** */
package org.calypsonet.keyple.demo.control.data

import java.time.LocalDateTime
import org.calypsonet.keyple.card.storagecard.StorageCardExtensionService
import org.calypsonet.keyple.demo.common.constant.CardConstant
import org.calypsonet.keyple.demo.common.model.EventStructure
import org.calypsonet.keyple.demo.common.model.type.PriorityCode
import org.calypsonet.keyple.demo.common.model.type.VersionNumber
import org.calypsonet.keyple.demo.common.parser.SCContractStructureParser
import org.calypsonet.keyple.demo.common.parser.SCEnvironmentHolderStructureParser
import org.calypsonet.keyple.demo.common.parser.SCEventStructureParser
import org.calypsonet.keyple.demo.control.data.model.AppSettings
import org.calypsonet.keyple.demo.control.data.model.CardReaderResponse
import org.calypsonet.keyple.demo.control.data.model.Contract
import org.calypsonet.keyple.demo.control.data.model.Location
import org.calypsonet.keyple.demo.control.data.model.Status
import org.calypsonet.keyple.demo.control.data.model.Validation
import org.calypsonet.keyple.demo.control.data.model.mapper.ContractMapper
import org.calypsonet.keyple.demo.control.data.model.mapper.ValidationMapper
import org.eclipse.keypop.reader.CardReader
import org.eclipse.keypop.storagecard.card.StorageCard
import org.eclipse.keypop.storagecard.transaction.ChannelControl
import timber.log.Timber

class StorageCardRepository {

  fun executeControlProcedure(
      controlDateTime: LocalDateTime,
      cardReader: CardReader,
      storageCard: StorageCard,
      locations: List<Location>
  ): CardReaderResponse {

    var errorMessage: String?
    val errorTitle: String? = null
    var validation: Validation? = null
    var status: Status = Status.ERROR

    val storageCardExtension = StorageCardExtensionService.getInstance()

    try {
      // Create a card transaction for control
      val cardTransaction =
          try {
            storageCardExtension.createStorageCardTransactionManager(cardReader, storageCard)
          } catch (e: Exception) {
            Timber.w(e)
            throw RuntimeException("Failed to create storage card transaction", e)
          }

      // Step 2 - Read and unpack environment, event and contract structures
      cardTransaction
          .prepareReadBlocks(
              CardConstant.SC_ENVIRONMENT_AND_HOLDER_FIRST_BLOCK,
              CardConstant.SC_ENVIRONMENT_AND_HOLDER_LAST_BLOCK)
          .prepareReadBlocks(CardConstant.SC_EVENT_FIRST_BLOCK, CardConstant.SC_EVENT_LAST_BLOCK)
          .prepareReadBlocks(
              CardConstant.SC_CONTRACT_FIRST_BLOCK, CardConstant.SC_COUNTER_LAST_BLOCK)
          .processCommands(ChannelControl.KEEP_OPEN)

      // Step 2 - Unpack environment structure
      val environmentContent =
          storageCard.getBlocks(
              CardConstant.SC_ENVIRONMENT_AND_HOLDER_FIRST_BLOCK,
              CardConstant.SC_ENVIRONMENT_AND_HOLDER_LAST_BLOCK)
      val env = SCEnvironmentHolderStructureParser().parse(environmentContent)

      // Step 3 - If EnvVersionNumber of the Environment structure is not the expected one (==1 for
      // the current version) reject the card.
      if (env.envVersionNumber != VersionNumber.CURRENT_VERSION) {
        throw EnvironmentException("wrong version number")
      }

      // Step 4 - If EnvEndDate points to a date in the past reject the card.
      if (env.envEndDate.getDate().isBefore(controlDateTime.toLocalDate())) {
        throw EnvironmentException("End date expired")
      }

      // Step 5 - Read and unpack the event record
      val eventContent =
          storageCard.getBlocks(CardConstant.SC_EVENT_FIRST_BLOCK, CardConstant.SC_EVENT_LAST_BLOCK)
      val event = SCEventStructureParser().parse(eventContent)

      // Step 6 - If EventVersionNumber is not the expected one (==1 for the current version) reject
      // the card (if ==0 return error status indicating clean card).
      val eventVersionNumber = event.eventVersionNumber
      if (eventVersionNumber != VersionNumber.CURRENT_VERSION) {
        if (eventVersionNumber == VersionNumber.UNDEFINED) {
          throw EventCleanCardException()
        } else {
          throw EventWrongVersionNumberException()
        }
      }

      var contractEventValid = true
      val contractUsed = event.eventContractUsed

      val eventValidityEndDate =
          event.eventDatetime.plusMinutes(AppSettings.validationPeriod.toLong())

      // Step 7 - If EventLocation != value configured in the control terminal set the validated
      // contract valid flag as false and go to point CNT_READ.
      if (AppSettings.location.id != event.eventLocation) {
        contractEventValid = false
      }
      // Step 8 - Else If EventDateStamp points to a date in the past
      // -> set the validated contract valid flag as false and go to point CNT_READ.
      else if (event.eventDatetime.isBefore(controlDateTime.toLocalDate().atStartOfDay())) {
        contractEventValid = false
      }

      // Step 9 - Else If (EventTimeStamp + Validation period configure in the control terminal) <
      // current time of the control terminal
      //  -> set the validated contract valid flag as false.
      else if (eventValidityEndDate.isBefore(controlDateTime)) {
        contractEventValid = false
      }

      // Step 10 - CNT_READ: Read contract data (already read above)
      val contractContent =
          storageCard.getBlocks(
              CardConstant.SC_CONTRACT_FIRST_BLOCK, CardConstant.SC_COUNTER_LAST_BLOCK)
      val contract = SCContractStructureParser().parse(contractContent)

      // Create validation if event is valid
      if (isValidEvent(event)) {
        validation = ValidationMapper.map(event = event, contract = contract, locations = locations)
      }

      val displayedContract = arrayListOf<Contract>()
      val record = 1 // Storage card has only one contract
      var contractExpired = false
      var contractValidated = false

      if (contract.contractVersionNumber == VersionNumber.UNDEFINED) {
        // Step 13 - If the ContractVersionNumber == 0 then the contract is blank
      } else if (contract.contractVersionNumber != VersionNumber.CURRENT_VERSION) {
        // Step 14 - If ContractVersionNumber is not the expected one (==1 for the current
        // version) reject the card.
        throw RuntimeException("Contract Version Number error (!= CURRENT_VERSION)")
      } else {
        // Step 15 - If ContractAuthenticator is not 0 perform the verification
        @Suppress("ControlFlowWithEmptyBody")
        if (contract.contractAuthenticator != 0) {
          // Step 15.1 & 15.2 - TODO: SAM verification steps
        }

        // Step 16 - If ContractValidityEndDate points to a date in the past mark contract as
        // expired.
        if (contract.contractValidityEndDate.getDate().isBefore(controlDateTime.toLocalDate())) {
          contractExpired = true
        }

        // Step 17 - If EventContractUsed points to the current contract index
        // & not valid flag is false then mark it as Validated.
        if (contractUsed == record && contractEventValid) {
          contractValidated = true
        }

        var validationDateTime: LocalDateTime? = null
        if (contractValidated) {
          validationDateTime = event.eventDatetime
        }

        // Step 18 - If the ContractTariff value for the contract is 2 or 3, extract the counter
        // value.
        val nbTicketsLeft =
            if (contract.contractTariff == PriorityCode.MULTI_TRIP ||
                contract.contractTariff == PriorityCode.STORED_VALUE) {
              contract.counterValue
            } else {
              null
            }

        // Step 19 - Add contract data to the list of contracts read to return to the upper layer.
        displayedContract.add(
            ContractMapper.map(
                contract = contract,
                record = record,
                contractExpired = contractExpired,
                contractValidated = contractValidated,
                validationDateTime = validationDateTime,
                nbTicketsLeft = nbTicketsLeft))
      }

      Timber.i("Control procedure result: STATUS_OK")
      status = Status.TICKETS_FOUND

      // Step 20 - Close the transaction
      cardTransaction.processCommands(ChannelControl.CLOSE_AFTER)

      var validationList: ArrayList<Validation>? = null
      if (validation != null) {
        validationList = arrayListOf(validation)
      }

      // Step 21 - Return the status of the operation to the upper layer. <Exit process>
      return CardReaderResponse(
          status = status, lastValidationsList = validationList, titlesList = displayedContract)
    } catch (e: Exception) {
      errorMessage = e.message
      Timber.e(e)
      when (e) {
        is EnvironmentException -> {
          errorMessage = "Environment error: $errorMessage"
        }
        is EventCleanCardException -> {
          status = Status.EMPTY_CARD
        }
        is EventWrongVersionNumberException -> {
          status = Status.ERROR
        }
        else -> {
          status = Status.ERROR
        }
      }
    }

    return CardReaderResponse(
        status = status,
        titlesList = arrayListOf(),
        errorTitle = errorTitle,
        errorMessage = errorMessage)
  }

  /**
   * An event is considered valid for display if an eventTimeStamp or an eventDateStamp has been set
   * during a previous validation
   */
  private fun isValidEvent(event: EventStructure): Boolean {
    return event.eventTimeStamp.value != 0 || event.eventDateStamp.value != 0
  }

  private class EnvironmentException(message: String) : RuntimeException(message)

  private class EventCleanCardException : RuntimeException("clean card")

  private class EventWrongVersionNumberException : RuntimeException("wrong version number")
}
