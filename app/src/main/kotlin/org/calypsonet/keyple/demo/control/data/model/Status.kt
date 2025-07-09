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
package org.calypsonet.keyple.demo.control.data.model

enum class Status(val status: String) {
  LOADING("loading"),
  ERROR("error"),
  TICKETS_FOUND("tickets_found"),
  INVALID_CARD("invalid_card"),
  EMPTY_CARD("empty_card"),
  WRONG_CARD("wrong_card"),
  DEVICE_CONNECTED("device_connected"),
  SUCCESS("success")
}
