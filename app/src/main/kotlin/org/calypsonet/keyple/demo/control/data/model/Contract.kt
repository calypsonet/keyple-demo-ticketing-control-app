/* ******************************************************************************
 * Copyright (c) 2021 Calypso Networks Association https://calypsonet.org/
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which is available at
 * https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: MIT
 ****************************************************************************** */
package org.calypsonet.keyple.demo.control.data.model

import android.os.Parcelable
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contract(
    val name: String?,
    val valid: Boolean,
    val validationDateTime: LocalDateTime?,
    val record: Int,
    val expired: Boolean,
    val contractValidityStartDate: LocalDate,
    val contractValidityEndDate: LocalDate,
    val nbTicketsLeft: Int? = null
) : Parcelable
