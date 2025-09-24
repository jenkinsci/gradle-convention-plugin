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

import info.solidsoft.gradle.pitest.PitestPlugin
import info.solidsoft.gradle.pitest.PitestPluginExtension
import io.github.aaravmahajanofficial.extensions.quality.QualityExtension
import io.github.aaravmahajanofficial.utils.hasJavaSources
import io.github.aaravmahajanofficial.utils.versionFromCatalogOrFail
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.kotlin.dsl.configure

internal fun Project.configurePitMutation(
    ext: QualityExtension,
    libs: VersionCatalog,
) {
    if (!ext.pitest.enabled.get() || !hasJavaSources()) return

    pluginManager.apply(PitestPlugin::class.java)

    configure<PitestPluginExtension> {
        threads.set(ext.pitest.threads)
        pitestVersion.set(versionFromCatalogOrFail(libs, "pit"))
        targetClasses.set(ext.pitest.targetClasses)
        excludedClasses.set(ext.pitest.excludedClasses)
        mutationThreshold.set(ext.pitest.mutationThreshold)
        outputFormats.set(ext.pitest.outputFormats)
        mutators.set(ext.pitest.mutators)
    }

    tasks.named("check").configure { t ->
        t.dependsOn("pitest")
    }
}
