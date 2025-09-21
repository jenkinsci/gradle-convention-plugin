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
    private val extension: FrontendExtension,
) {
    public fun configure() {
        extension.enabled.convention(project.isFrontendProject())

        if (!extension.enabled.get()) return

        project.pluginManager.apply(NodePlugin::class.java)

        configureNodeExtension()
        configureTasks()
    }

    private fun configureNodeExtension() {
        project.configure<NodeExtension> {
            nodeProjectDir.set(extension.nodeProjectDir)

            download.set(extension.download)
            version.set(extension.nodeVersion)

            if (extension.npmVersion.isPresent && extension.npmVersion.get().isNotBlank()) {
                npmVersion.set(extension.npmVersion)
            }

            if (extension.yarnVersion.isPresent && extension.yarnVersion.get().isNotBlank()) {
                yarnVersion.set(extension.yarnVersion)
            }

            distBaseUrl.set(extension.nodeDownloadRoot.map { it.toString() })
            allowInsecureProtocol.set(false)

            npmInstallCommand.set(extension.npmInstallCommand)

            workDir.set(extension.workDir)
            npmWorkDir.set(extension.npmWorkDir)
            yarnWorkDir.set(extension.yarnWorkDir)
            nodeProxySettings.set(extension.nodeProxySettings)

            if (extension.packageManager.get() == PackageManager.YARN_COREPACK) {
                download.set(false)
            }
        }
    }

    private fun configureTasks() {
        when (extension.packageManager.get()) {
            PackageManager.NPM -> configureNpmTasks()
            PackageManager.YARN,
            PackageManager.YARN_COREPACK,
            -> configureYarnTasks()
        }
    }

    private fun npmArgsFor(script: String): List<String> =
        buildList {
            extension.logLevel
                .get()
                .takeIf { it.isNotBlank() }
                ?.let {
                    add("--loglevel")
                    add(it)
                }
            add("run")
            add(script)
        }

    private fun configureNpmTasks() {
        val npmInstall = project.tasks.named<NpmInstallTask>("npmInstall")

        val frontendBuild =
            project.tasks.register<NpmTask>("frontendBuild") {
                dependsOn(npmInstall)
                group = "Frontend"
                description = "Build frontend assets"
                args.set(npmArgsFor(extension.buildScript.get()))

                configureTaskInputsOutputs(this)

                onlyIf { hasScriptDefined(extension.buildScript.get()) }
            }

        val frontendTest =
            project.tasks.register<NpmTask>("frontendTest") {
                dependsOn(npmInstall)
                group = "Frontend"
                description = "Run frontend tests"
                args.set(npmArgsFor(extension.testScript.get()))
                ignoreExitValue.set(extension.testFailureIgnore.get())

                onlyIf { hasScriptDefined(extension.testScript.get()) && !extension.skipTests.get() }
            }

        val frontendLint =
            project.tasks.register<NpmTask>("frontendLint") {
                dependsOn(npmInstall)
                group = "Frontend"
                description = "Lint frontend code"
                args.set(npmArgsFor(extension.lintScript.get()))

                onlyIf { hasScriptDefined(extension.lintScript.get()) && !extension.skipLint.get() }
            }

        project.tasks.register<NpmTask>("frontendDev") {
            dependsOn(npmInstall)
            group = "Frontend"
            description = "Start development server"
            args.set(npmArgsFor(extension.devScript.get()))

            onlyIf { hasScriptDefined(extension.devScript.get()) }
        }

        integrationWithGradleLifecycle(registerAssetCopy(frontendBuild), frontendTest, frontendLint)
    }

    private fun configureYarnTasks() {
        val yarnInstall = project.tasks.named<YarnInstallTask>("yarnInstall")

        val frontendBuild =
            project.tasks.register<YarnTask>("frontendBuild") {
                dependsOn(yarnInstall)
                group = "Frontend"
                description = "Build frontend assets"
                args.set(listOf("run", extension.buildScript.get()))

                configureTaskInputsOutputs(this)

                onlyIf { hasScriptDefined(extension.buildScript.get()) }
            }

        val frontendTest =
            project.tasks.register<YarnTask>("frontendTest") {
                dependsOn(yarnInstall)
                group = "Frontend"
                description = "Run frontend tests"
                args.set(listOf("run", extension.testScript.get()))
                ignoreExitValue.set(extension.testFailureIgnore.get())

                onlyIf { hasScriptDefined(extension.testScript.get()) && !extension.skipTests.get() }
            }

        val frontendLint =
            project.tasks.register<YarnTask>("frontendLint") {
                dependsOn(yarnInstall)
                group = "Frontend"
                description = "Lint frontend code"
                args.set(listOf("run", extension.lintScript.get()))

                onlyIf { hasScriptDefined(extension.lintScript.get()) && !extension.skipLint.get() }
            }

        project.tasks.register<YarnTask>("frontendDev") {
            dependsOn(yarnInstall)
            group = "Frontend"
            description = "Start development server"
            args.set(listOf("run", extension.devScript.get()))

            onlyIf { hasScriptDefined(extension.devScript.get()) }
        }

        integrationWithGradleLifecycle(registerAssetCopy(frontendBuild), frontendTest, frontendLint)
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
                task.outputs.dir(extension.destDir)
            }

            is YarnTask -> {
                task.inputs.files(inputFiles + project.file("yarn.lock"))
                task.outputs.dir(extension.destDir)
            }
        }
    }

    private fun registerAssetCopy(buildTask: TaskProvider<*>): TaskProvider<Copy> =
        project.tasks.register<Copy>("syncFrontendAssets") {
            group = "Frontend"
            description = "Copy built frontend assets into resources for packaging"
            dependsOn(buildTask)
            from(extension.destDir)
            into(project.layout.projectDirectory.dir("src/main/webapp/frontend"))
        }

    private fun integrationWithGradleLifecycle(
        syncAssetsTask: TaskProvider<Copy>,
        testTask: TaskProvider<*>,
        lintTask: TaskProvider<*>,
    ) {
        project.tasks.named("processResources").configure { it.dependsOn(syncAssetsTask) }

        project.tasks.named("test").configure { it.dependsOn(testTask) }

        project.tasks.named("check").configure { it.dependsOn(lintTask) }
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
