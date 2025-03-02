/* ******************************************************************************
 * Copyright (c) 2021 Calypso Networks Association https://calypsonet.org/
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which is available at
 * https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: MIT
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
