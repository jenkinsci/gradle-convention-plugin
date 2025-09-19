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
package io.github.aaravmahajanofficial.extensions

import com.github.gradle.node.npm.proxy.ProxySettings
import io.github.aaravmahajanofficial.constants.ConfigurationConstants.Frontend.NPM_LOG_LEVEL
import io.github.aaravmahajanofficial.constants.ConfigurationConstants.Frontend.SKIP_LINT
import io.github.aaravmahajanofficial.constants.ConfigurationConstants.Frontend.SKIP_TESTS
import io.github.aaravmahajanofficial.constants.ConfigurationConstants.Frontend.TEST_FAILURE_IGNORE
import io.github.aaravmahajanofficial.utils.gradleProperty
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.property
import java.net.URI
import javax.inject.Inject

public open class FrontendExtension
@Inject
constructor(
    objects: ObjectFactory,
    layout: ProjectLayout,
    providers: ProviderFactory,
) {
    public val enabled: Property<Boolean> = objects.property<Boolean>().convention(false)

    public val download: Property<Boolean> = objects.property<Boolean>().convention(true)
    public val nodeVersion: Property<String> = objects.property<String>().convention("24.8.0")
    public val npmVersion: Property<String> = objects.property<String>()
    public val yarnVersion: Property<String> = objects.property<String>()

    public val packageManager: Property<PackageManager> =
        objects.property<PackageManager>().convention(PackageManager.NPM)

    public val nodeDownloadRoot: Property<URI> =
        objects.property<URI>().convention(URI.create("https://repo.jenkins-ci.org/nodejs-dist"))

    public val npmInstallCommand: Property<String> = objects.property<String>().convention("install")

    public val skipTests: Property<Boolean> = objects.property<Boolean>().convention(
        gradleProperty(
            providers,
            SKIP_TESTS, String::toBoolean,
        ).orElse(false),
    )

    public val skipLint: Property<Boolean> = objects.property<Boolean>().convention(
        gradleProperty(
            providers,
            SKIP_LINT, String::toBoolean,
        ).orElse(false),
    )

    public val testFailureIgnore: Property<Boolean> = objects.property<Boolean>().convention(
        gradleProperty(
            providers,
            TEST_FAILURE_IGNORE, String::toBoolean,
        ).orElse(false),
    )

    public val logLevel: Property<String> =
        objects.property<String>().convention(gradleProperty(providers, NPM_LOG_LEVEL).orElse(""))

    public val workDir: DirectoryProperty =
        objects.directoryProperty().convention(layout.projectDirectory.dir(".gradle/nodejs"))

    public val npmWorkDir: DirectoryProperty =
        objects.directoryProperty().convention(layout.projectDirectory.dir(".gradle/npm"))

    public val yarnWorkDir: DirectoryProperty =
        objects.directoryProperty().convention(layout.projectDirectory.dir(".gradle/yarn"))

    public val nodeProjectDir: DirectoryProperty = objects.directoryProperty().convention(layout.projectDirectory)

    public val nodeProxySettings: Property<ProxySettings> =
        objects.property<ProxySettings>().convention(ProxySettings.SMART)

    public val buildScript: Property<String> = objects.property<String>().convention("build")
    public val testScript: Property<String> = objects.property<String>().convention("test")
    public val lintScript: Property<String> = objects.property<String>().convention("lint")
    public val devScript: Property<String> = objects.property<String>().convention("dev")
}

public enum class PackageManager {
    NPM,
    YARN,
    YARN_COREPACK,
}
