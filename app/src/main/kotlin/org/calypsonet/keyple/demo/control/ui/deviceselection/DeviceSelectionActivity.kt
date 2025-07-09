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
package org.calypsonet.keyple.demo.control.ui.deviceselection

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.nfc.NfcManager
import android.os.Bundle
import org.calypsonet.keyple.demo.control.BuildConfig
import org.calypsonet.keyple.demo.control.R
import org.calypsonet.keyple.demo.control.data.model.AppSettings
import org.calypsonet.keyple.demo.control.data.model.ReaderType
import org.calypsonet.keyple.demo.control.databinding.ActivityDeviceSelectionBinding
import org.calypsonet.keyple.demo.control.ui.BaseActivity
import org.calypsonet.keyple.demo.control.ui.SettingsActivity
import org.calypsonet.keyple.plugin.bluebird.BluebirdConstants
import org.calypsonet.keyple.plugin.flowbird.FlowbirdPlugin

class DeviceSelectionActivity : BaseActivity() {

  private lateinit var activityDeviceSelectionBinding: ActivityDeviceSelectionBinding

  private val mock: String = "Mock"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityDeviceSelectionBinding = ActivityDeviceSelectionBinding.inflate(layoutInflater)
    setContentView(activityDeviceSelectionBinding.root)
    activityDeviceSelectionBinding.appVersion.text =
        getString(R.string.version, BuildConfig.VERSION_NAME)
    // Bluebird
    if (BluebirdConstants.PLUGIN_NAME.contains(mock)) {
      activityDeviceSelectionBinding.bluebirdBtn.setBackgroundColor(Color.GRAY)
    } else {
      activityDeviceSelectionBinding.bluebirdBtn.setOnClickListener {
        AppSettings.readerType = ReaderType.BLUEBIRD
        val permissions: MutableList<String> =
            mutableListOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                "com.bluebird.permission.SAM_DEVICE_ACCESS")
        val granted = PermissionHelper.checkPermission(this, permissions.toTypedArray())
        if (granted) {
          startActivity(Intent(this, SettingsActivity::class.java))
          finish()
        }
      }
    }
    // Coppernic
    activityDeviceSelectionBinding.coppernicBtn.setOnClickListener {
      AppSettings.readerType = ReaderType.COPPERNIC
      startActivity(Intent(this, SettingsActivity::class.java))
      finish()
    }
    // Famoco
    activityDeviceSelectionBinding.famocoBtn.setOnClickListener {
      AppSettings.readerType = ReaderType.FAMOCO
      startActivity(Intent(this, SettingsActivity::class.java))
      finish()
    }
    // Flowbird
    if (FlowbirdPlugin.PLUGIN_NAME.contains(mock)) {
      activityDeviceSelectionBinding.flowbirdBtn.setBackgroundColor(Color.GRAY)
    } else {
      activityDeviceSelectionBinding.flowbirdBtn.setOnClickListener {
        AppSettings.readerType = ReaderType.FLOWBIRD
        startActivity(Intent(this, SettingsActivity::class.java))
        finish()
      }
    }
    // Standard NFC terminal
    activityDeviceSelectionBinding.nfcTerminalBtn.setOnClickListener {
      val nfcManager = getSystemService(NFC_SERVICE) as NfcManager
      if (nfcManager.defaultAdapter?.isEnabled == true) {
        AppSettings.readerType = ReaderType.NFC_TERMINAL
        startActivity(Intent(this, SettingsActivity::class.java))
        finish()
      } else {
        EnableNfcDialog().apply {
          show(supportFragmentManager, EnableNfcDialog::class.java.simpleName)
        }
      }
    }
  }

  @SuppressLint("MissingSuperCall")
  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<out String>,
      grantResults: IntArray
  ) {
    when (requestCode) {
      PermissionHelper.MY_PERMISSIONS_REQUEST_ALL -> {
        if (grantResults.isNotEmpty()) {
          for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
              PermissionDeniedDialog().apply {
                show(supportFragmentManager, PermissionDeniedDialog::class.java.simpleName)
              }
              return
            }
          }
          startActivity(Intent(applicationContext, SettingsActivity::class.java))
          finish()
        } else {
          PermissionDeniedDialog().apply {
            show(supportFragmentManager, PermissionDeniedDialog::class.java.simpleName)
          }
        }
        return
      }
      // Add other 'when' lines to check for other
      // permissions this app might request.
      else -> {
        // Ignore all other requests.
      }
    }
  }
}
