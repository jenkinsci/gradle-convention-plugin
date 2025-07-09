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
package constants

public object PluginMetadata {
    public const val ID: String = "io.jenkins.gradle.convention"
    public const val EXTENSION_NAME: String = "jenkinsConvention"
    public const val DISPLAY_NAME: String = "Jenkins Gradle Convention Plugin"
    public const val VERSION: String = "1.0.0"
    public const val TASK_GROUP: String = "Jenkins"
    public const val MIN_GRADLE_VERSION: String = "9.0"
}
