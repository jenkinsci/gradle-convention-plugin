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

public class LanguagePluginValidator
    @Inject
    constructor(
        private val project: Project,
        private val problems: Problems,
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
                                "Add 'org.jetbrains.kotlin.jvm' to your plugins block: plugins { id(\"org.jetbrains.kotlin.jvm\") }",
                            ).severity(Severity.ERROR)
                            .contextualLabel("No language plugin detected in project '${project.name}'")
                    }
                }
            }
        }
    }
