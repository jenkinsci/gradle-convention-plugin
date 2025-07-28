/*
 * Copyright 2025 Aarav Mahajan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package io.github.aaravmahajanofficial.utils

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.provider.Provider
import kotlin.jvm.optionals.getOrElse

internal fun versionFromCatalogOrFail(
    libs: VersionCatalog,
    alias: String,
): String =
    libs
        .findVersion(alias)
        .getOrElse {
            error(
                "Version '$alias' missing from version catalog. Please update 'libs.versions.toml'.",
            )
        }.requiredVersion

internal fun libraryFromCatalog(
    libs: VersionCatalog,
    alias: String,
): Provider<MinimalExternalModuleDependency> = libs.findLibrary(alias).get()
