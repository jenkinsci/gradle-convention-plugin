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
package io.github.aaravmahajanofficial.constants

import java.net.URI

public object UrlConstants {
    public const val JENKINS_INCREMENTALS_REPO_URL: String = "https://repo.jenkins-ci.org/incrementals"
    public val JENKINS_PUBLIC_REPO_URL: URI = URI.create("https://repo.jenkins-ci.org/public")
}
