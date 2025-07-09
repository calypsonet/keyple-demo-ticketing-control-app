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

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import org.calypsonet.keyple.demo.control.R

class PermissionDeniedDialog : DialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return activity?.let {
      // Use the Builder class for convenient dialog construction
      val builder = AlertDialog.Builder(it)
      builder.setCancelable(false).setMessage(R.string.permission_denied_message).setPositiveButton(
          android.R.string.cancel) { _, _ ->
            dismiss()
            it.finish()
          }
      // Create the AlertDialog object and return it
      builder.create()
    } ?: throw IllegalStateException("Activity cannot be null")
  }
}
