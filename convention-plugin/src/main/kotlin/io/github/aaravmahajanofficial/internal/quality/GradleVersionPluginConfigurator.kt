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
package io.github.aaravmahajanofficial.internal.quality

import com.github.benmanes.gradle.versions.VersionsPlugin
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.github.aaravmahajanofficial.extensions.quality.QualityExtension
import io.github.aaravmahajanofficial.utils.isNonStable
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType

internal fun Project.configureGradleVersionPlugin(quality: QualityExtension) {
    if (!quality.versions.enabled.get()) return

    pluginManager.apply(VersionsPlugin::class.java)

    tasks.withType<DependencyUpdatesTask>().configureEach { t ->
        t.rejectVersionIf {
            isNonStable(it.candidate.version) && !isNonStable(it.currentVersion)
        }
    }
}
