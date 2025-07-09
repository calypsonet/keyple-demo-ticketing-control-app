/* ******************************************************************************
 * Copyright (c) 2021 Calypso Networks Association https://calypsonet.org/
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
package org.calypsonet.keyple.demo.control.data.model.mapper

import java.time.LocalDateTime
import org.calypsonet.keyple.demo.common.model.ContractStructure
import org.calypsonet.keyple.demo.control.data.model.Contract

object ContractMapper {
  fun map(
      contract: ContractStructure,
      record: Int,
      contractValidated: Boolean,
      contractExpired: Boolean,
      validationDateTime: LocalDateTime?,
      nbTicketsLeft: Int?
  ): Contract {
    return Contract(
        name = contract.contractTariff.value,
        valid = contractValidated,
        record = record,
        validationDateTime = validationDateTime,
        expired = contractExpired,
        contractValidityStartDate = contract.contractSaleDate.getDate(),
        contractValidityEndDate = contract.contractValidityEndDate.getDate(),
        nbTicketsLeft = nbTicketsLeft)
  }
}
