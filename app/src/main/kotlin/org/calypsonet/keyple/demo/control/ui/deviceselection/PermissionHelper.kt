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

import android.app.Activity
import android.content.pm.PackageManager

object PermissionHelper {

  const val MY_PERMISSIONS_REQUEST_ALL = 1000

  fun checkPermission(activity: Activity, permissions: Array<String>): Boolean {
    val permissionDenied = permissions.filter { !isPermissionGranted(activity, it) }
    if (permissionDenied.isNotEmpty()) {
      val permissionsToAsk = arrayOfNulls<String>(permissionDenied.size)
      for ((position, permission) in permissionDenied.withIndex()) {
        permissionsToAsk[position] = permission
      }
      activity.requestPermissions(permissionsToAsk, MY_PERMISSIONS_REQUEST_ALL)
      return false
    }
    return true
  }

  private fun isPermissionGranted(activity: Activity, permission: String): Boolean {
    return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
  }
}
