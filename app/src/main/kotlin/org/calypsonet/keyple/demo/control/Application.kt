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
package org.calypsonet.keyple.demo.control

import android.content.Context
import androidx.multidex.MultiDex
import dagger.android.DaggerApplication
import org.calypsonet.keyple.demo.control.di.AppComponent
import org.calypsonet.keyple.demo.control.di.DaggerAppComponent
import timber.log.Timber
import timber.log.Timber.DebugTree

class Application : DaggerApplication() {

  override fun attachBaseContext(context: Context?) {
    super.attachBaseContext(context)
    MultiDex.install(this)
  }

  override fun onCreate() {
    super.onCreate()
    Timber.plant(DebugTree())
  }

  override fun applicationInjector(): AppComponent {
    return DaggerAppComponent.builder().application(this).build()
  }
}
