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
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.owasp.dependencycheck.gradle.DependencyCheckPlugin
import org.owasp.dependencycheck.gradle.extension.DependencyCheckExtension

internal fun Project.configureOwaspDependencyCheck(ext: QualityExtension) {
    if (!ext.owaspDependencyCheck.enabled.get()) return

    // Apply plugin immediately if not already applied
    pluginManager.apply(DependencyCheckPlugin::class.java)

    configure<DependencyCheckExtension> {
        failBuildOnCVSS.set(ext.owaspDependencyCheck.failOnCvss)
        formats.set(ext.owaspDependencyCheck.formats)
        suppressionFiles.set(
            ext.owaspDependencyCheck.suppressionFiles
                .get()
                .map { it.asFile.absolutePath },
        )
        outputDirectory.set(
            ext.owaspDependencyCheck.outputDirectory
                .get()
                .asFile,
        )
        data.directory.set(
            ext.owaspDependencyCheck.dataDirectory
                .get()
                .asFile.absolutePath,
        )
        scanConfigurations.set(ext.owaspDependencyCheck.scanConfigurations)
    }

    tasks.named("check").configure { t ->
        t.dependsOn("dependencyCheckAnalyze")
    }
}
