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

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.problems.ProblemGroup
import org.gradle.api.problems.ProblemId
import org.gradle.api.problems.ProblemReporter
import org.gradle.api.problems.Problems
import org.gradle.api.problems.Severity
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import javax.inject.Inject

@SuppressWarnings("INCUBATING")
public class LanguagePluginValidator
    @Inject
    constructor(
        private val project: Project,
        problems: Problems,
    ) {
        public companion object {
            private val PROBLEM_GROUP = ProblemGroup.create("jenkins-gradle-convention", "Jenkins Gradle Convention")
        }

        private val problemReporter: ProblemReporter = problems.getReporter()

        public fun validate() {
            project.afterEvaluate {
                val hasJava = project.plugins.hasPlugin(JavaPlugin::class.java)
                val hasKotlin = project.plugins.hasPlugin(KotlinPluginWrapper::class.java)

                if (!hasJava && !hasKotlin) {
                    val problemId =
                        ProblemId.create(
                            "missing-language-plugin",
                            "Missing Language Plugin",
                            PROBLEM_GROUP,
                        )

                    problemReporter.report(problemId) { builder ->
                        builder
                            .details(
                                "The 'io.jenkins.gradle.convention' plugin requires a language plugin to be applied. " +
                                    "Please apply either 'java-library' or 'org.jetbrains.kotlin.jvm' to your project.",
                            ).solution("Add 'java-library' to your plugins block: plugins { id(\"java-library\") }")
                            .solution(
                                "Add 'org.jetbrains.kotlin.jvm' to your plugins block:" +
                                    " plugins { id(\"org.jetbrains.kotlin.jvm\") }",
                            ).severity(Severity.ERROR)
                            .contextualLabel("No language plugin detected in project '${project.name}'")
                    }
                }
            }
        }
    }
