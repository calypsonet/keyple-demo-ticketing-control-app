/* ******************************************************************************
 * Copyright (c) 2021 Calypso Networks Association https://calypsonet.org/
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which is available at
 * https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: MIT
 ****************************************************************************** */
package org.calypsonet.keyple.demo.control.di.scope

import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import javax.inject.Scope

/**
 * In Dagger, an unscoped component cannot depend on a scoped component. As [AppComponent] is a
 * scoped component @AppScoped, we create a custom scope to be used by all fragment components.
 * Additionally, a component with a specific scope cannot have a sub component with the same scope.
 */
@Documented @Scope @Retention(RetentionPolicy.RUNTIME) annotation class ActivityScoped
