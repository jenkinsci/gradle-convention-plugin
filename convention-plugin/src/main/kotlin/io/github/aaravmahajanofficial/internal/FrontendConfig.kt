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
package io.github.aaravmahajanofficial.internal

import com.github.gradle.node.NodeExtension
import com.github.gradle.node.NodePlugin
import com.github.gradle.node.npm.task.NpmInstallTask
import com.github.gradle.node.npm.task.NpmTask
import com.github.gradle.node.yarn.task.YarnInstallTask
import com.github.gradle.node.yarn.task.YarnTask
import io.github.aaravmahajanofficial.extensions.FrontendExtension
import io.github.aaravmahajanofficial.extensions.PackageManager
import io.github.aaravmahajanofficial.utils.isFrontendProject
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.json.JSONObject

public class FrontendConfig(
    private val project: Project,
    private val ext: FrontendExtension,
) {
    public fun configure() {
        ext.enabled.convention(project.isFrontendProject())

        if (!ext.enabled.get()) return

        project.pluginManager.apply(NodePlugin::class.java)

        configureNodeExtension()
        configureTasks()
    }

    private fun configureNodeExtension() {
        project.configure<NodeExtension> {
            nodeProjectDir.set(ext.nodeProjectDir)

            download.set(ext.download)
            version.set(ext.nodeVersion)

            if (ext.npmVersion.get().isNotBlank()) {
                npmVersion.set(ext.npmVersion)
            }

            if (ext.yarnVersion.get().isNotBlank()) {
                yarnVersion.set(ext.yarnVersion)
            }

            distBaseUrl.set(ext.nodeDownloadRoot.map { it.toString() })
            allowInsecureProtocol.set(false)

            npmInstallCommand.set(ext.npmInstallCommand)

            workDir.set(ext.workDir)
            npmWorkDir.set(ext.npmWorkDir)
            yarnWorkDir.set(ext.yarnWorkDir)
            nodeProxySettings.set(ext.nodeProxySettings)

            if (ext.packageManager.get() == PackageManager.YARN_COREPACK) {
                download.set(false)
            }
        }
    }

    private fun configureTasks() {
        when (ext.packageManager.get()) {
            PackageManager.NPM -> configureNpm()
            PackageManager.YARN,
            PackageManager.YARN_COREPACK,
            -> configureYarn()
        }
    }

    private fun baseScriptArgs(script: String): List<String> =
        buildList {
            ext.logLevel
                .get()
                .takeIf { it.isNotBlank() }
                ?.let {
                    add("--loglevel")
                    add(it)
                }
            add("run")
            add(script)
        }

    private fun configureNpm() {
        val npmInstall = project.tasks.named<NpmInstallTask>("npmInstall")

        val build =
            project.tasks.register<NpmTask>("frontendBuild") {
                dependsOn(npmInstall)
                group = "Frontend"
                description = "Build frontend assets"
                args.set(baseScriptArgs(ext.buildScript.get()))
                configureTaskInputsOutputs(this)

                onlyIf { hasScriptDefined(ext.buildScript.get()) }
            }

        val test =
            project.tasks.register<NpmTask>("frontendTest") {
                dependsOn(npmInstall)
                group = "Frontend"
                description = "Run frontend tests."
                args.set(baseScriptArgs(ext.testScript.get()))
                ignoreExitValue.set(ext.testFailureIgnore.get())

                onlyIf { hasScriptDefined(ext.testScript.get()) && !ext.skipTests.get() }
            }

        val lint =
            project.tasks.register<NpmTask>("frontendLint") {
                dependsOn(npmInstall)
                group = "Frontend"
                description = "Run frontend linter."
                args.set(baseScriptArgs(ext.lintScript.get()))

                onlyIf { hasScriptDefined(ext.lintScript.get()) && !ext.skipLint.get() }
            }

        project.tasks.register<NpmTask>("frontendDev") {
            dependsOn(npmInstall)
            group = "Frontend"
            description = "Start frontend development server."
            args.set(baseScriptArgs(ext.devScript.get()))

            onlyIf { hasScriptDefined(ext.devScript.get()) }
        }

        integrationWithGradleLifecycle(build, test, lint)
    }

    private fun configureYarn() {
        val yarnInstall = project.tasks.named<YarnInstallTask>("yarnInstall")

        val build =
            project.tasks.register<YarnTask>("frontendBuild") {
                dependsOn(yarnInstall)
                group = "Frontend"
                description = "Build frontend assets."
                args.set(baseScriptArgs(ext.buildScript.get()))
                configureTaskInputsOutputs(this)

                onlyIf { hasScriptDefined(ext.buildScript.get()) }
            }

        val test =
            project.tasks.register<YarnTask>("frontendTest") {
                dependsOn(yarnInstall)
                group = "Frontend"
                description = "Run frontend tests."
                args.set(baseScriptArgs(ext.testScript.get()))
                ignoreExitValue.set(ext.testFailureIgnore.get())

                onlyIf { hasScriptDefined(ext.testScript.get()) && !ext.skipTests.get() }
            }

        val lint =
            project.tasks.register<YarnTask>("frontendLint") {
                dependsOn(yarnInstall)
                group = "Frontend"
                description = "Run frontend linter."
                args.set(baseScriptArgs(ext.lintScript.get()))

                onlyIf { hasScriptDefined(ext.lintScript.get()) && !ext.skipLint.get() }
            }

        project.tasks.register<YarnTask>("frontendDev") {
            dependsOn(yarnInstall)
            group = "Frontend"
            description = "Start frontend development server."
            args.set(baseScriptArgs(ext.devScript.get()))

            onlyIf { hasScriptDefined(ext.devScript.get()) }
        }

        integrationWithGradleLifecycle(build, test, lint)
    }

    private fun configureTaskInputsOutputs(task: Any) {
        val inputFiles =
            listOf(
                project.fileTree("src/main/js"),
                project.fileTree("src/main/ts"),
                project.fileTree("src/main/webapp"),
                project.file("package.json"),
                project.file("webpack.config.js"),
                project.file("tsconfig.json"),
                project.file("babel.config.js"),
                project.file(".env"),
            )

        when (task) {
            is NpmTask -> {
                task.inputs.files(inputFiles + project.file("package-lock.json"))
                task.outputs.dir(ext.destDir)
            }

            is YarnTask -> {
                task.inputs.files(inputFiles + project.file("yarn.lock"))
                task.outputs.dir(ext.destDir)
            }
        }
    }

    private fun integrationWithGradleLifecycle(
        buildTask: TaskProvider<*>,
        testTask: TaskProvider<*>,
        lintTask: TaskProvider<*>,
    ) {
        val copyAssetsTask = registerAssetCopy(buildTask)

        project.tasks.named("processResources").configure { it.dependsOn(copyAssetsTask) }

        project.tasks.named("test").configure { it.dependsOn(testTask) }

        project.tasks.named("check").configure { it.dependsOn(lintTask) }
    }

    private fun registerAssetCopy(buildTask: TaskProvider<*>): TaskProvider<Copy> =
        project.tasks.register<Copy>("syncFrontendAssets") {
            group = "Frontend"
            description = "Copy built frontend assets into resources for packaging"
            dependsOn(buildTask)
            from(ext.destDir)
            into(project.layout.projectDirectory.dir("src/main/webapp/frontend"))

            outputs.cacheIf { true }
        }

    private fun hasScriptDefined(scriptName: String): Boolean {
        val packageJson = project.file("package.json")
        if (!packageJson.exists()) return false

        return try {
            val content = packageJson.readText(Charsets.UTF_8)
            val json = JSONObject(content)
            val scripts = json.optJSONObject("scripts")
            scripts?.has(scriptName) == true
        } catch (_: Exception) {
            false
        }
    }
}
