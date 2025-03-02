/* ******************************************************************************
 * Copyright (c) 2021 Calypso Networks Association https://calypsonet.org/
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which is available at
 * https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: MIT
 ****************************************************************************** */
package org.calypsonet.keyple.demo.control.data.model.mapper

import org.calypsonet.keyple.demo.common.model.ContractStructure
import org.calypsonet.keyple.demo.common.model.EventStructure
import org.calypsonet.keyple.demo.control.data.model.Location
import org.calypsonet.keyple.demo.control.data.model.Validation

object ValidationMapper {
  fun map(
      event: EventStructure,
      contract: ContractStructure?,
      locations: List<Location>
  ): Validation {
    val name = contract?.contractTariff?.value ?: "Event"
    return Validation(
        name = name,
        dateTime = event.eventDatetime,
        location = LocationMapper.map(locations, event),
        destination = null,
        provider = null)
  }
}
