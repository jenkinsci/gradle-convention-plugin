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
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.plugins.quality.CheckstylePlugin
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.gradle.language.base.plugins.LifecycleBasePlugin

internal fun Project.configureCheckstyle(
    quality: QualityExtension,
    libs: VersionCatalog,
) {
    if (!quality.checkstyle.enabled.get() || !project.hasJavaSources()) return

    project.pluginManager.apply(CheckstylePlugin::class.java)

    project.configure<CheckstyleExtension> {
        toolVersion = versionFromCatalogOrFail(libs, "checkstyle")
        configFile = resolveConfigFile("checkstyle", "checkstyle.xml").asFile
        isIgnoreFailures = !quality.checkstyle.failOnViolation.get()

        val suppressionsFile = resolveConfigFile("checkstyle", "suppressions.xml").asFile
        if (suppressionsFile.exists()) {
            configProperties = mapOf("suppressions.file" to suppressionsFile.absolutePath)
        }
    }
    project.tasks.withType<Checkstyle>().configureEach { task ->
        task.group = LifecycleBasePlugin.VERIFICATION_GROUP
        task.description = "Runs Checkstyle."

        val javaSources =
            project
                .the<JavaPluginExtension>()
                .sourceSets
                .getByName("main")
                .allSource
        task.source = javaSources.plus(quality.checkstyle.source.get()).asFileTree
        task.include(quality.checkstyle.include.get())
        task.exclude(excludeList.plus(quality.checkstyle.exclude.get()))

        task.reports {
            it.xml.required.set(true)
            it.html.required.set(true)
            it.sarif.required.set(true)
        }
    }
    project.tasks.named("check").configure { t ->
        t.dependsOn("checkstyleMain")
    }
}
