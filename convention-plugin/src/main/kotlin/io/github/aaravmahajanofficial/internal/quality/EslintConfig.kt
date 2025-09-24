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

import com.github.gradle.node.NodePlugin
import com.github.gradle.node.npm.task.NpmTask
import io.github.aaravmahajanofficial.extensions.quality.QualityExtension
import io.github.aaravmahajanofficial.utils.isFrontendProject
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import org.gradle.language.base.plugins.LifecycleBasePlugin

internal fun Project.configureEsLint(ext: QualityExtension) {
    if (!ext.eslint.enabled.get() || !isFrontendProject()) {
        return
    }

    pluginManager.apply(NodePlugin::class.java)

    tasks.register<NpmTask>("eslint") {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "Run ESLint."

        dependsOn("npmInstall")

        val configFile =
            ext.eslint.configFile.orNull
                ?.asFile
                ?.absolutePath

        doFirst {
            val argsList = mutableListOf("run", "lint")
            if (ext.eslint.autofix
                    .get()
            ) {
                argsList += "--fix"
            }
            configFile?.let { file -> argsList += listOf("--config", file) }
            args.set(argsList)
        }
        inputs.files(
            fileTree("src/main/js"),
            fileTree("src/main/ts"),
            fileTree("src/main/webapp"),
            file("package.json"),
            file("package-lock.json"),
        )
        outputs.dir(file("build/eslint-reports"))
    }

    tasks.named("check").configure { t ->
        t.dependsOn("eslint")
    }
}
