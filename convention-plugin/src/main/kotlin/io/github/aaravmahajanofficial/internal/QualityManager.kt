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
@file:Suppress("TooManyFunctions", "ktlint:standard:no-wildcard-imports", "LongMethod", "WildcardImport")

package io.github.aaravmahajanofficial.internal

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import com.github.benmanes.gradle.versions.VersionsPlugin
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.gradle.node.NodePlugin
import com.github.gradle.node.npm.task.NpmTask
import com.github.spotbugs.snom.SpotBugsExtension
import com.github.spotbugs.snom.SpotBugsPlugin
import com.github.spotbugs.snom.SpotBugsTask
import de.aaschmid.gradle.plugins.cpd.Cpd
import de.aaschmid.gradle.plugins.cpd.CpdExtension
import de.aaschmid.gradle.plugins.cpd.CpdPlugin
import info.solidsoft.gradle.pitest.PitestPlugin
import info.solidsoft.gradle.pitest.PitestPluginExtension
import io.github.aaravmahajanofficial.extensions.QualityExtension
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import kotlinx.kover.gradle.plugin.KoverGradlePlugin
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.quality.*
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.owasp.dependencycheck.gradle.DependencyCheckPlugin
import org.owasp.dependencycheck.gradle.extension.DependencyCheckExtension

public class QualityManager(
    private val project: Project,
    private val quality: QualityExtension,
) {
    public fun apply() {
        configureSpotless()
        configureCheckstyle()
        configureCodenarc()
        configureSpotBugs()
        configurePmd()
        configureJacoco()
        configureDetekt()
        configurePitMutation()
        configureKoverExtension()
        configureOwaspDependencyCheck()
        configureGradleVersionPlugin()
        configureEsLint()
        configureDokka()
        configureCpd()
        configureCheckTask()
    }

    private fun configureCheckstyle() {
        if (!quality.checkstyle.enabled.get() || !project.isJavaProject()) return

        project.pluginManager.apply(CheckstylePlugin::class.java)

        project.configure<CheckstyleExtension> {
            toolVersion = quality.checkstyle.toolVersion.get()
            configFile = resolveConfigFile("checkstyle", "checkstyle.xml").asFile
            isIgnoreFailures = !quality.checkstyle.failOnViolation.get()

            val suppressionsFile = resolveConfigFile("checkstyle", "suppressions.xml").asFile
            if (suppressionsFile.exists()) {
                configProperties = mapOf("suppressions.file" to suppressionsFile.absolutePath)
            }
        }
        project.tasks.withType<Checkstyle>().configureEach { task ->
            task.group = "Verification"
            task.description = "Runs Checkstyle."

            task.source = project.fileTree(quality.checkstyle.source)
            task.include(quality.checkstyle.include.get())
            task.exclude(quality.checkstyle.exclude.get())

            task.reports {
                it.xml.required.set(true)
                it.html.required.set(true)
                it.sarif.required.set(true)
            }
        }
    }

    private fun configureCodenarc() {
        if (!quality.codenarc.enabled.get() || !project.isGroovyProject()) return

        project.pluginManager.apply(CodeNarcPlugin::class.java)

        project.configure<CodeNarcExtension> {
            toolVersion = quality.codenarc.toolVersion.get()
            isIgnoreFailures = !quality.codenarc.failOnViolation.get()
        }
        project.tasks.withType<CodeNarc>().configureEach { task ->
            task.group = "Verification"
            task.description = "Runs Codenarc."
            task.reports {
                it.xml.required.set(true)
                it.html.required.set(true)
            }
            task.configFile =
                resolveConfigFile(
                    toolName = "codenarc",
                    if (task.name.contains("Test", ignoreCase = true)) {
                        "rules-test.groovy"
                    } else {
                        "rules.groovy"
                    },
                ).asFile
        }
    }

    private fun configureSpotBugs() {
        if (!quality.spotbugs.enabled.get() || !project.isJavaProject()) return

        project.pluginManager.apply(SpotBugsPlugin::class.java)

        project.configure<SpotBugsExtension> {
            toolVersion.set(quality.spotbugs.toolVersion)
            effort.set(quality.spotbugs.effortLevel.get())
            reportLevel.set(quality.spotbugs.reportLevel.get())
            ignoreFailures.set(!quality.spotbugs.failOnError.get())
            excludeFilter.set(resolveConfigFile("spotbugs", "excludesFilter.xml"))
        }

        project.tasks.withType<SpotBugsTask>().configureEach {
            it.reports.create("xml") { report ->
                report.required.set(true)
            }
            it.reports.create("html") { report ->
                report.required.set(true)
            }
            it.reports.create("sarif") { report ->
                report.required.set(true)
            }
        }
    }

    private fun configurePmd() {
        if (!quality.pmd.enabled.get() || !project.isJavaProject()) return

        project.pluginManager.apply(PmdPlugin::class.java)

        project.configure<PmdExtension> {
            toolVersion = quality.pmd.toolVersion.get()
            ruleSetFiles = project.files(resolveConfigFile("pmd", "pmd-ruleset.xml"))
            isConsoleOutput = quality.pmd.consoleOutput.get()
            isIgnoreFailures = !quality.pmd.failOnViolation.get()
        }
        project.tasks.withType<Pmd>().configureEach { task ->
            task.group = "Verification"
            task.description = "Run PMD."

            task.source = project.fileTree(quality.pmd.source)
            task.include(quality.pmd.include.get())
            task.exclude(quality.pmd.exclude.get())

            task.reports { reports ->
                reports.xml.required.set(true)
                reports.html.required.set(true)
            }
        }
    }

    private fun configureJacoco() {
        if (!quality.jacoco.enabled.get()) return

        project.pluginManager.apply(JacocoPlugin::class.java)

        project.configure<JacocoPluginExtension> {
            toolVersion = quality.jacoco.toolVersion.get()
        }
        project.tasks.withType<Test>().configureEach { t ->
            t.finalizedBy("jacocoTestReport")
        }
        project.tasks.withType<JacocoReport>().configureEach { jacocoReport ->
            jacocoReport.dependsOn(project.tasks.withType<Test>())
            jacocoReport.reports { t ->
                t.xml.required.set(true)
                t.html.required.set(true)
                t.csv.required.set(true)
            }

            val allExcludes = quality.jacoco.excludes.get()
            val classDirectories =
                project
                    .fileTree(
                        project.layout.buildDirectory
                            .dir("classes")
                            .get(),
                    ).apply {
                        exclude(allExcludes)
                    }
            jacocoReport.classDirectories.setFrom(classDirectories)
        }
        project.tasks.withType<JacocoCoverageVerification>().configureEach { t ->
            t.dependsOn("jacocoTestReport")
            t.violationRules { rules ->
                rules.rule { rule ->
                    rule.excludes = quality.jacoco.excludes.get()
                    rule.limit {
                        it.counter = "LINE"
                        it.value = "COVEREDRATIO"
                        it.minimum =
                            quality.jacoco.minimumCodeCoverage
                                .get()
                                .toBigDecimal()
                    }
                }
            }
        }
    }

    private fun configureDetekt() {
        if (!quality.detekt.enabled.get() || !project.isKotlinProject()) return

        project.pluginManager.apply(DetektPlugin::class.java)

        project.configure<DetektExtension> {
            toolVersion = quality.detekt.toolVersion.get()
            autoCorrect = quality.detekt.autoCorrect.get()
            buildUponDefaultConfig = true
            isIgnoreFailures = !quality.detekt.failOnViolation.get()
            source.setFrom(quality.detekt.source)
            config.setFrom(resolveConfigFile("detekt", "detekt.yml"))
            baseline = resolveConfigFile("detekt", "detekt-baseline.xml").asFile
            parallel = true
        }
        project.tasks.withType<Detekt>().configureEach { detekt ->
            detekt.reports {
                it.xml.required.set(true)
                it.html.required.set(true)
                it.sarif.required.set(true)
            }
        }
    }

    private fun configureSpotless() {
        if (!quality.spotless.enabled.get()) return

        project.pluginManager.apply(SpotlessPlugin::class.java)

        project.variantResolution("spotless")

        project.configure<SpotlessExtension> {
            kotlin {
                it.target("**/*.kt")
                it.targetExclude("**/build/**", "bin/**", "**/generated/**")
                it.ktlint()
                it.trimTrailingWhitespace()
                it.endWithNewline()
            }
            kotlinGradle {
                it.target("*.gradle.kts", "**/*.gradle.kts")
                it.targetExclude("**/build/**", "**/.gradle/**")
                it.ktlint()
                it.trimTrailingWhitespace()
                it.endWithNewline()
            }
            java {
                it.target("src/*/java/**/*.java")
                it.targetExclude("**/generated/**", "**/build/**", "**/.gradle/**")
                it.palantirJavaFormat()
                it.trimTrailingWhitespace()
                it.endWithNewline()
                it.removeUnusedImports()
            }
            format("misc") {
                it.target(
                    "*.md",
                    "*.txt",
                    ".gitignore",
                    ".gitattributes",
                    "*.properties",
                    "*.yml",
                    "*.yaml",
                    "*.json",
                    ".editorconfig",
                    "*.xml",
                    "*.gradle",
                    "*.sh",
                    "*.dockerfile",
                    "Dockerfile*",
                )
                it.targetExclude(
                    "**/build/**",
                    "**/.gradle/**",
                    "**/.idea/**",
                    "**/node_modules/**",
                    "**/.git/**",
                    "**/generated/**",
                )

                it.trimTrailingWhitespace()
                it.endWithNewline()
            }
        }
    }

    private fun configureOwaspDependencyCheck() {
        if (!quality.owaspDependencyCheck.enabled.get()) return

        // Apply plugin immediately if not already applied
        project.pluginManager.apply(DependencyCheckPlugin::class.java)

        project.configure<DependencyCheckExtension> {
            failBuildOnCVSS = quality.owaspDependencyCheck.failOnCvss.get()
            formats = quality.owaspDependencyCheck.formats.get()
            suppressionFiles =
                quality.owaspDependencyCheck.suppressionFiles
                    .get()
                    .map { it.asFile.absolutePath }
            outputDirectory =
                quality.owaspDependencyCheck.outputDirectory
                    .get()
                    .asFile.absolutePath
            data.directory =
                quality.owaspDependencyCheck.dataDirectory
                    .get()
                    .asFile.absolutePath
            scanConfigurations = quality.owaspDependencyCheck.scanConfigurations.get()
        }
    }

    private fun configurePitMutation() {
        if (!quality.pitest.enabled.get()) return

        project.pluginManager.apply(PitestPlugin::class.java)

        project.configure<PitestPluginExtension> {
            threads.set(quality.pitest.threads)
            pitestVersion.set(quality.pitest.pitVersion.get())
            targetClasses.set(quality.pitest.targetClasses)
            excludedClasses.set(quality.pitest.excludedClasses)
            mutationThreshold.set(quality.pitest.mutationThreshold)
            outputFormats.set(quality.pitest.outputFormats)
            mutators.set(quality.pitest.mutators)
        }
    }

    private fun configureGradleVersionPlugin() {
        if (!quality.versions.enabled.get()) return

        project.pluginManager.apply(VersionsPlugin::class.java)

        project.tasks.withType<DependencyUpdatesTask>().configureEach { t ->
            t.rejectVersionIf {
                isNonStable(it.candidate.version) && !isNonStable(it.currentVersion)
            }
        }
    }

    private fun configureKoverExtension() {
        if (!quality.kover.enabled.get() || !project.isKotlinProject()) return

        project.pluginManager.apply(KoverGradlePlugin::class.java)

        project.configure<KoverProjectExtension> {
            reports { rep ->
                rep.total { total ->
                    total.xml {
                        it.onCheck.set(true)
                    }
                    total.html {
                        it.onCheck.set(true)
                    }
                }
                rep.verify { verify ->
                    verify.rule { rule ->
                        rule.bound {
                            it.minValue.set(quality.kover.coverageThreshold)
                        }
                    }
                }
            }
        }
    }

    private fun configureEsLint() {
        if (!quality.eslint.enabled.get() ||
            !project.isFrontendProject()
        ) {
            return
        }

        project.pluginManager.apply(NodePlugin::class.java)

        project.tasks.register<NpmTask>("eslint") {
            group = "Verification"
            description = "Run ESLint."

            dependsOn("npmInstall")

            val configFile =
                quality.eslint.configFile.orNull
                    ?.asFile
                    ?.absolutePath

            doFirst {
                val argsList = mutableListOf("run", "lint")
                if (quality.eslint.autofix
                        .get()
                ) {
                    argsList += "--fix"
                }
                configFile?.let { argsList += listOf("--config", it) }
                args.set(argsList)
            }
            inputs.files(
                project.fileTree("src/main/js"),
                project.fileTree("src/main/ts"),
                project.fileTree("src/main/webapp"),
                project.file("package.json"),
                project.file("package-lock.json"),
            )
            outputs.dir(project.file("build/eslint-reports"))
        }
    }

    private fun configureDokka() {
        if (!quality.dokka.enabled.get() || !project.isKotlinProject()) return

        project.pluginManager.apply(DokkaPlugin::class.java)

        project.tasks.named("dokkaHtml").configure { task ->
            task.outputs.dir(quality.dokka.outputDirectory)
        }
    }

    private fun configureCpd() {
        if (!quality.cpd.enabled.get() || !project.isJavaProject()) return

        project.pluginManager.apply(CpdPlugin::class.java)

        project.configure<CpdExtension> {
            toolVersion = quality.pmd.toolVersion.get()
            isIgnoreFailures = !quality.cpd.failOnViolation.get()
            minimumTokenCount = quality.cpd.minimumTokenCount.get()
        }
        project.tasks.withType<Cpd>().configureEach { task ->
            task.group = "Verification"
            task.description = "Runs CPD."

            task.source = project.fileTree(quality.cpd.source)

            task.reports {
                it.xml.required.set(true)
                it.text.required.set(true)
            }
        }
    }

    private fun configureCheckTask() {
        project.tasks.named("check").configure { checkTask ->

            val isJava = project.isJavaProject()
            val isKotlin = project.isKotlinProject()
            val isGroovy = project.isGroovyProject()
            val isFrontEnd = project.isFrontendProject()

            val toolTasks =
                listOfNotNull(
                    if (isJava && quality.checkstyle.enabled.get()) "checkstyleMain" else null,
                    if (isGroovy && quality.codenarc.enabled.get()) "codenarcMain" else null,
                    if (isJava && quality.spotbugs.enabled.get()) "spotbugsMain" else null,
                    if (isJava && quality.pmd.enabled.get()) "pmdMain" else null,
                    if (quality.spotless.enabled.get()) "spotlessCheck" else null,
                    if (isKotlin && quality.detekt.enabled.get()) "detekt" else null,
                    if (quality.owaspDependencyCheck.enabled.get()) "dependencyCheckAnalyze" else null,
                    if (quality.jacoco.enabled.get()) "jacocoTestCoverageVerification" else null,
                    if (isJava && quality.cpd.enabled.get()) "cpdCheck" else null,
                    if (isKotlin && quality.kover.enabled.get()) "koverVerify" else null,
                    if (quality.pitest.enabled.get()) "pitest" else null,
                    if (isFrontEnd && quality.eslint.enabled.get()) "eslint" else null,
                )

            toolTasks.forEach { checkTask.dependsOn(it) }
        }
    }

    private fun resolveConfigFile(
        toolName: String,
        fileName: String,
    ): RegularFile {
        val configPath = "config/$toolName/$fileName"
        val userConfig = project.layout.projectDirectory.file(configPath)

        if (userConfig.asFile.exists()) return userConfig

        val resourceUrl =
            javaClass.classLoader.getResource("defaults/$toolName/$fileName")
                ?: error("Missing built-in $toolName config file in plugin resources: $fileName. This is a bug.")

        userConfig.asFile.parentFile.mkdirs()

        resourceUrl
            .openStream()
            .use { input -> userConfig.asFile.outputStream().use { output -> input.copyTo(output) } }

        return userConfig
    }
}

private fun Project.variantResolution(config: String) {
    configurations.matching { it.name.startsWith(config) }.configureEach {
        it.attributes { at ->
            at.attribute(
                LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
                project.objects.named(LibraryElements.JAR),
            )
            at.attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage.JAVA_RUNTIME))
        }
    }
}

private fun Project.isJavaProject(): Boolean {
    val isJava = plugins.hasPlugin("java")
    val isJavaLibrary = plugins.hasPlugin("java-library")
    val isJavaGradlePlugin = plugins.hasPlugin("java-gradle-plugin")
    return isJava || isJavaLibrary || isJavaGradlePlugin
}

private fun Project.isKotlinProject(): Boolean = plugins.hasPlugin("org.jetbrains.kotlin.jvm")

private fun Project.isGroovyProject(): Boolean = plugins.hasPlugin("groovy")

private fun Project.isFrontendProject(): Boolean {
    val frontendRoots =
        listOf(
            "package.json",
            "yarn.lock",
            "pnpm-lock.yaml",
        )

    if (frontendRoots.any { file(it).exists() }) return true

    val frontendDirs = listOf("src/main/js", "src/main/ts", "src/main/webapp")
    return frontendDirs.any { file(it).exists() && file(it).isDirectory }
}

private fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}
