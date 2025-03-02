/* ******************************************************************************
 * Copyright (c) 2021 Calypso Networks Association https://calypsonet.org/
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which is available at
 * https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: MIT
 ****************************************************************************** */
package org.calypsonet.keyple.demo.control.di

import android.content.Context
import dagger.Binds
import dagger.Module
import org.calypsonet.keyple.demo.control.Application

@Suppress("unused")
@Module
abstract class AppModule {
  // expose Application as an injectable context
  @Binds abstract fun bindContext(application: Application): Context
}
