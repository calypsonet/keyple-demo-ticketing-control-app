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

import android.content.Intent
import android.os.Bundle
import java.util.Timer
import java.util.TimerTask
import org.calypsonet.keyple.demo.control.databinding.ActivityMainBinding
import org.calypsonet.keyple.demo.control.ui.deviceselection.DeviceSelectionActivity

class MainActivity : BaseActivity() {
  private lateinit var activityMainBinding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    // Make sure this is before calling super.onCreate
    super.onCreate(savedInstanceState)
    activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(activityMainBinding.root)
    Timer()
        .schedule(
            object : TimerTask() {
              override fun run() {
                if (!isFinishing) {
                  startActivity(Intent(applicationContext, DeviceSelectionActivity::class.java))
                  finish()
                }
              }
            },
            SPLASH_MAX_DELAY_MS.toLong())
  }

  companion object {
    private const val SPLASH_MAX_DELAY_MS = 2000
  }
}
