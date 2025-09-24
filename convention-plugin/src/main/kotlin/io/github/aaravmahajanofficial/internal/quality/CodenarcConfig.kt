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
import io.github.aaravmahajanofficial.utils.hasGroovySources
import io.github.aaravmahajanofficial.utils.resolveConfigFile
import io.github.aaravmahajanofficial.utils.versionFromCatalogOrFail
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.plugins.quality.CodeNarc
import org.gradle.api.plugins.quality.CodeNarcExtension
import org.gradle.api.plugins.quality.CodeNarcPlugin
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.language.base.plugins.LifecycleBasePlugin

internal fun Project.configureCodenarc(
    ext: QualityExtension,
    libs: VersionCatalog,
) {
    if (!ext.codenarc.enabled.get() || !hasGroovySources()) return

    pluginManager.apply(CodeNarcPlugin::class.java)

    configure<CodeNarcExtension> {
        toolVersion = versionFromCatalogOrFail(libs, "codenarc")
        isIgnoreFailures = !ext.codenarc.failOnViolation.get()
    }
    tasks.withType<CodeNarc>().configureEach { task ->
        task.group = LifecycleBasePlugin.VERIFICATION_GROUP
        task.description = "Runs Codenarc."
        task.reports {
            it.xml.required.set(true)
            it.html.required.set(true)
        }
        task.configFile =
            resolveConfigFile(
                toolName = "codenarc",
                fileName =
                    if (task.name.contains("Test", ignoreCase = true)) {
                        "rules-test.groovy"
                    } else {
                        "rules.groovy"
                    },
            ).asFile

        task.source =
            project
                .files(
                    "src/main/groovy",
                    "src/test/groovy",
                    "src/main/resources",
                ).plus(ext.codenarc.source.get())
                .asFileTree
                .matching {
                    it.include("**/*.groovy")
                }
    }

    tasks.named("check").configure { t ->
        t.dependsOn("codenarcMain")
    }
}
