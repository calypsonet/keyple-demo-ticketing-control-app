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

import org.calypsonet.keyple.demo.common.model.EventStructure
import org.calypsonet.keyple.demo.control.data.model.Location

object LocationMapper {
  fun map(locations: List<Location>, event: EventStructure): Location {
    return locations.filter { event.eventLocation == it.id }[0]
  }
}
