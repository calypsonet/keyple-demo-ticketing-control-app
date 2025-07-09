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
import dagger.Provides
import org.calypsonet.keyple.demo.control.data.ReaderRepository
import org.calypsonet.keyple.demo.control.di.scope.AppScoped
import org.eclipse.keypop.reader.spi.CardReaderObservationExceptionHandlerSpi
import timber.log.Timber

@Suppress("unused")
@Module
class ReaderModule {

  @Provides
  @AppScoped
  fun provideReaderRepository(
      cardReaderObservationExceptionHandlerSpi: CardReaderObservationExceptionHandlerSpi
  ): ReaderRepository = ReaderRepository(cardReaderObservationExceptionHandlerSpi)

  @Provides
  @AppScoped
  fun provideCardReaderObservationExceptionHandlerSpi(): CardReaderObservationExceptionHandlerSpi =
      CardReaderObservationExceptionHandlerSpi { pluginName, readerName, e ->
        Timber.e("An unexpected reader error occurred: $pluginName:$readerName: $e")
      }
}
