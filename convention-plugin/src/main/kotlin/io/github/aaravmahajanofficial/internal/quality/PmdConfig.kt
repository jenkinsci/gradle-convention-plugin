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

import io.github.aaravmahajanofficial.extensions.quality.QualityExtension
import io.github.aaravmahajanofficial.extensions.quality.excludeList
import io.github.aaravmahajanofficial.utils.hasJavaSources
import io.github.aaravmahajanofficial.utils.resolveConfigFile
import io.github.aaravmahajanofficial.utils.versionFromCatalogOrFail
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.plugins.quality.Pmd
import org.gradle.api.plugins.quality.PmdExtension
import org.gradle.api.plugins.quality.PmdPlugin
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.language.base.plugins.LifecycleBasePlugin

internal fun Project.configurePmd(
    quality: QualityExtension,
    libs: VersionCatalog,
) {
    if (!quality.pmd.enabled.get() || !hasJavaSources()) return

    pluginManager.apply(PmdPlugin::class.java)

    configure<PmdExtension> {
        toolVersion = versionFromCatalogOrFail(libs, "pmd")
        ruleSetFiles = files(resolveConfigFile("pmd", "pmd-ruleset.xml"))
        isConsoleOutput = quality.pmd.consoleOutput.get()
        isIgnoreFailures = !quality.pmd.failOnViolation.get()
    }
    tasks.withType<Pmd>().configureEach { task ->
        task.group = LifecycleBasePlugin.VERIFICATION_GROUP
        task.description = "Runs PMD."

        task.source = files("src/main/java").plus(quality.pmd.source.get()).asFileTree
        task.include(quality.pmd.include.get())
        task.exclude(excludeList.plus(quality.pmd.exclude.get()))

        task.reports { reports ->
            reports.xml.required.set(true)
            reports.html.required.set(true)
        }
    }
    tasks.named("check").configure { t ->
        t.dependsOn("pmdMain")
    }
}
