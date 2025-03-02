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
import dagger.Module
import dagger.Provides
import org.calypsonet.keyple.demo.control.data.LocationRepository
import org.calypsonet.keyple.demo.control.di.scope.AppScoped

@Suppress("unused")
@Module
class LocationModule {

  @Provides
  @AppScoped
  fun providesLocationRepository(context: Context): LocationRepository = LocationRepository(context)
}
