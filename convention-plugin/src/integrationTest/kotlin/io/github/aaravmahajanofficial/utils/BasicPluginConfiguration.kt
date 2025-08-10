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
package io.github.aaravmahajanofficial.utils

fun basicPluginConfiguration(
    applyKotlin: Boolean = false,
    bomBlock: String = "",
    qualityBlock: String = "",
    dependenciesBlock: String = "",
): String =
    """
    plugins {
        ${if (applyKotlin) "kotlin(\"jvm\")" else ""}
        id("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin") version "999-SNAPSHOT"
    }

    jenkinsConvention {
        artifactId = "test-plugin"
        homePage = uri("https://github.com")

        developers {
            developer {
                id = "dev-123"
                name = "Test Dev"
                email = "testDev@gmail.com"
            }
        }

        licenses {
            license {
                name = "MIT"
                url = uri("https://opensource.org/license/mit")
            }
        }

        ${bomBlock.ifBlank { "" }}
        ${qualityBlock.ifBlank { "" }}
    }

    ${dependenciesBlock.ifBlank { "" }}

    """.trimIndent()
