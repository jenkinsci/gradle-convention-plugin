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

import de.aaschmid.gradle.plugins.cpd.Cpd
import de.aaschmid.gradle.plugins.cpd.CpdExtension
import de.aaschmid.gradle.plugins.cpd.CpdPlugin
import io.github.aaravmahajanofficial.extensions.quality.QualityExtension
import io.github.aaravmahajanofficial.utils.versionFromCatalogOrFail
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.language.base.plugins.LifecycleBasePlugin

internal fun Project.configureCpd(
    ext: QualityExtension,
    libs: VersionCatalog,
) {
    if (!ext.cpd.enabled.get()) return

    project.pluginManager.apply(CpdPlugin::class.java)

    project.configure<CpdExtension> {
        toolVersion = versionFromCatalogOrFail(libs, "pmd")
        isIgnoreFailures = !ext.cpd.failOnViolation.get()
        minimumTokenCount = ext.cpd.minimumTokenCount.get()
    }
    project.tasks.withType<Cpd>().configureEach { task ->
        task.group = LifecycleBasePlugin.VERIFICATION_GROUP
        task.description = "Runs CPD."

        task.source =
            project
                .files(
                    "src/main/java",
                    "src/main/groovy",
                ).plus(ext.cpd.source.get())
                .asFileTree
                .matching {
                    it.include("**/*.java", "**/*.groovy")
                }

        task.reports {
            it.xml.required.set(true)
            it.text.required.set(true)
        }
    }

    project.tasks.named("check").configure { t ->
        t.dependsOn("cpdCheck")
    }
}
