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

fun mockBuildScript(
    content: String = "",
    bomBlock: String = "",
    qualityBlock: String = "",
    dependenciesBlock: String = "",
): String =
    """
    plugins {
        kotlin("jvm") version "2.2.20"
        id("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin") version "0.0.0-SNAPSHOT"
    }

    ${
        content.ifBlank {
            """
            jenkinsConvention {
                artifactId = "test-plugin"
                ${bomBlock.ifBlank { "" }}
                ${qualityBlock.ifBlank { "" }}
            }

            """.trimIndent()
        }
    }

    ${dependenciesBlock.ifBlank { "" }}
    """.trimIndent()
