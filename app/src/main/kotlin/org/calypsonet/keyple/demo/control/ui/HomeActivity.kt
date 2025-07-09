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
import org.calypsonet.keyple.demo.control.data.model.AppSettings
import org.calypsonet.keyple.demo.control.databinding.ActivityHomeBinding
import org.calypsonet.keyple.demo.control.databinding.LogoToolbarBinding

class HomeActivity : BaseActivity() {

  private lateinit var activityHomeBinding: ActivityHomeBinding
  private lateinit var logoToolbarBinding: LogoToolbarBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)
    logoToolbarBinding = activityHomeBinding.appBarLayout
    setContentView(activityHomeBinding.root)
    setSupportActionBar(logoToolbarBinding.toolbar)
    activityHomeBinding.locationSelected.text = AppSettings.location.toString()
    activityHomeBinding.startBtn.setOnClickListener {
      startActivity(Intent(this, ReaderActivity::class.java))
    }
  }
}
