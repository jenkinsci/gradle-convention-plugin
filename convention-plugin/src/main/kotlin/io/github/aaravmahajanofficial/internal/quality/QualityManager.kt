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
@file:Suppress("TooManyFunctions", "LongMethod")

package io.github.aaravmahajanofficial.internal.quality

import io.github.aaravmahajanofficial.constants.ConfigurationConstants.Quality.ENABLE_QUALITY_TOOLS
import io.github.aaravmahajanofficial.extensions.quality.QualityExtension
import io.github.aaravmahajanofficial.utils.gradleProperty
import io.github.aaravmahajanofficial.utils.libsCatalog
import org.gradle.api.Project

public class QualityManager(
    private val project: Project,
    private val quality: QualityExtension,
) {
    private val libs = project.libsCatalog()

    public fun apply() {
        if (!gradleProperty(project.providers, ENABLE_QUALITY_TOOLS, String::toBoolean).getOrElse(true)) {
            return
        }

        project.configureSpotless(quality, libs)
        project.configureCheckstyle(quality, libs)
        project.configureCodenarc(quality, libs)
        project.configureSpotBugs(quality)
        project.configurePmd(quality, libs)
        project.configureJacoco(quality, libs)
        project.configureDetekt(quality, libs)
        project.configurePitMutation(quality, libs)
        project.configureKoverExtension(quality)
        project.configureOwaspDependencyCheck(quality)
        project.configureGradleVersionPlugin(quality)
        project.configureEsLint(quality)
        project.configureDokka(quality)
        project.configureCpd(quality, libs)
    }
}
