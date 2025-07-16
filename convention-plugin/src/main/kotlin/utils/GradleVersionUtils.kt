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
package utils

import constants.PluginMetadata
import org.gradle.api.GradleException
import org.gradle.util.GradleVersion

public object GradleVersionUtils {
    public fun verifyGradleVersion() {
        val currentVersion = GradleVersion.current()
        val requiredVersion = GradleVersion.version(PluginMetadata.MIN_GRADLE_VERSION)

        if (currentVersion < requiredVersion) {
            throw GradleException(
                "${PluginMetadata.DISPLAY_NAME} requires Gradle ${PluginMetadata.MIN_GRADLE_VERSION} or higher. " +
                    "Current version is ${currentVersion.version}. Please upgrade your Gradle version.",
            )
        }
    }
}
