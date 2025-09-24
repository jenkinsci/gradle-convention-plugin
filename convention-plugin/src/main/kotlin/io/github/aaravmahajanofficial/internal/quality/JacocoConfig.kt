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
import io.github.aaravmahajanofficial.utils.versionFromCatalogOrFail
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

internal fun Project.configureJacoco(
    ext: QualityExtension,
    libs: VersionCatalog,
) {
    if (!ext.jacoco.enabled.get()) return

    project.pluginManager.apply(JacocoPlugin::class.java)

    project.configure<JacocoPluginExtension> {
        toolVersion = versionFromCatalogOrFail(libs, "jacoco")
    }

    val jacocoReportTasks = project.tasks.withType<JacocoReport>()
    val jacocoVerificationTasks = project.tasks.withType<JacocoCoverageVerification>()

    project.tasks.withType<Test>().configureEach { t ->
        t.finalizedBy(jacocoReportTasks)
    }

    jacocoReportTasks.configureEach { jacocoReport ->
        jacocoReport.reports { t ->
            t.xml.required.set(true)
            t.html.required.set(true)
            t.csv.required.set(false)
        }

        val allExcludes = ext.jacoco.excludes.get()
        val classDirectories =
            project
                .fileTree(
                    project.layout.buildDirectory
                        .dir("classes")
                        .get(),
                ).apply {
                    exclude(allExcludes)
                }
        jacocoReport.classDirectories.setFrom(classDirectories)
    }

    jacocoVerificationTasks.configureEach { t ->
        t.dependsOn(project.tasks.withType<JacocoReport>())
        t.violationRules { rules ->
            rules.rule { rule ->
                rule.excludes =
                    listOf(
                        "**/generated/**",
                        "**/target/**",
                        "**/build/**",
                        "**/Messages.class",
                        "**/*Descriptor.class",
                        "**/jelly/**",
                        "**/tags/**",
                    ).plus(ext.jacoco.excludes.get())
                rule.limit {
                    it.counter = "LINE"
                    it.value = "COVEREDRATIO"
                    it.minimum =
                        ext.jacoco.minimumCodeCoverage
                            .get()
                            .toBigDecimal()
                }
            }
        }
    }

    project.tasks.named("check").configure { t ->
        t.dependsOn("jacocoTestCoverageVerification")
    }
}
