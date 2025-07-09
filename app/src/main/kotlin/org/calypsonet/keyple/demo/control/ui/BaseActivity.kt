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
package org.calypsonet.keyple.demo.control.ui

import android.widget.Toast
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject
import org.calypsonet.keyple.demo.control.data.LocationRepository
import org.calypsonet.keyple.demo.control.domain.TicketingService

abstract class BaseActivity : DaggerAppCompatActivity() {

  @Inject lateinit var ticketingService: TicketingService
  @Inject lateinit var locationRepository: LocationRepository

  fun showToast(message: String) {
    runOnUiThread { Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show() }
  }
}
