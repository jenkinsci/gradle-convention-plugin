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
package internal

import constants.UrlConstants
import org.gradle.api.Project
import java.net.URI

public class RepositoryManager(
    private val project: Project,
) {
    public fun configure() {
        project.repositories.apply {
            maven {
                it.name = "JenkinsPublic"
                it.url = URI.create(UrlConstants.JENKINS_PUBLIC_REPO_URL)
                it.mavenContent { content ->
                    content.includeGroup("io.jenkins.plugins")
                    content.includeGroup("io.jenkins-ci.main")
                    content.includeGroup("io.jenkins-ci.plugins")
                    content.includeGroup("io.jenkins.tools.bom")
                }
            }
            mavenCentral()
            gradlePluginPortal()
        }
    }
}
