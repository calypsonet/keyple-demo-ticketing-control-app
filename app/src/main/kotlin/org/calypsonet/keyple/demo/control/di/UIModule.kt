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
package org.calypsonet.keyple.demo.control.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.calypsonet.keyple.demo.control.di.scope.ActivityScoped
import org.calypsonet.keyple.demo.control.ui.*
import org.calypsonet.keyple.demo.control.ui.cardcontent.CardContentActivity
import org.calypsonet.keyple.demo.control.ui.deviceselection.DeviceSelectionActivity

@Suppress("unused")
@Module
abstract class UIModule {

  @ActivityScoped @ContributesAndroidInjector abstract fun mainActivity(): MainActivity

  @ActivityScoped @ContributesAndroidInjector abstract fun settingsActivity(): SettingsActivity

  @ActivityScoped
  @ContributesAndroidInjector
  abstract fun deviceSelectionActivity(): DeviceSelectionActivity

  @ActivityScoped @ContributesAndroidInjector abstract fun homeActivity(): HomeActivity

  @ActivityScoped @ContributesAndroidInjector abstract fun readerActivity(): ReaderActivity

  @ActivityScoped
  @ContributesAndroidInjector
  abstract fun cardContentActivity(): CardContentActivity

  @ActivityScoped
  @ContributesAndroidInjector
  abstract fun networkInvalidActivity(): NetworkInvalidActivity
}
