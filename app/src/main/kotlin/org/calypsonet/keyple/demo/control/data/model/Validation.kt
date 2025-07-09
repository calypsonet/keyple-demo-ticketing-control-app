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

import android.os.Parcelable
import java.time.LocalDateTime
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Validation(
    val name: String,
    val location: Location,
    val destination: String?,
    val dateTime: LocalDateTime,
    val provider: Int? = null
) : Parcelable
