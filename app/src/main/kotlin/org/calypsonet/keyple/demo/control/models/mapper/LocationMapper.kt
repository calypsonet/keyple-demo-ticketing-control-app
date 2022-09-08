/* **************************************************************************************
 * Copyright (c) 2021 Calypso Networks Association https://calypsonet.org/
 *
 * See the NOTICE file(s) distributed with this work for additional information
 * regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 ************************************************************************************** */
package org.calypsonet.keyple.demo.control.models.mapper

import org.calypsonet.keyple.demo.common.parser.model.CardEvent
import org.calypsonet.keyple.demo.control.models.Location

object LocationMapper {
  fun map(locations: List<Location>, event: CardEvent): Location {
    return locations.filter { event.eventLocation == it.id }[0]
  }
}
