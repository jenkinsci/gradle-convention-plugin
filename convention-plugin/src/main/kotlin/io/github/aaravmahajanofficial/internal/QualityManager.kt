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
@file:Suppress("TooManyFunctions", "LongMethod")

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
import io.github.aaravmahajanofficial.constants.ConfigurationConstants.Quality.ENABLE_QUALITY_TOOLS
import io.github.aaravmahajanofficial.extensions.quality.QualityExtension
import io.github.aaravmahajanofficial.extensions.quality.excludeList
import io.github.aaravmahajanofficial.utils.gradleProperty
import io.github.aaravmahajanofficial.utils.versionFromCatalogOrFail
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import kotlinx.kover.gradle.plugin.KoverGradlePlugin
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.plugins.quality.CheckstylePlugin
import org.gradle.api.plugins.quality.CodeNarc
import org.gradle.api.plugins.quality.CodeNarcExtension
import org.gradle.api.plugins.quality.CodeNarcPlugin
import org.gradle.api.plugins.quality.Pmd
import org.gradle.api.plugins.quality.PmdExtension
import org.gradle.api.plugins.quality.PmdPlugin
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.gradle.language.base.plugins.LifecycleBasePlugin
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
    private val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

    public fun apply() {
        if (!gradleProperty(project.providers, ENABLE_QUALITY_TOOLS, String::toBoolean).getOrElse(true)) {
            return
        }

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
    }

    private fun configureCheckstyle() {
        if (!quality.checkstyle.enabled.get() || !project.hasJavaSources()) return

        project.pluginManager.apply(CheckstylePlugin::class.java)

        project.configure<CheckstyleExtension> {
            toolVersion = versionFromCatalogOrFail(libs, "checkstyle")
            configFile = resolveConfigFile("checkstyle", "checkstyle.xml").asFile
            isIgnoreFailures = !quality.checkstyle.failOnViolation.get()

            val suppressionsFile = resolveConfigFile("checkstyle", "suppressions.xml").asFile
            if (suppressionsFile.exists()) {
                configProperties = mapOf("suppressions.file" to suppressionsFile.absolutePath)
            }
        }
        project.tasks.withType<Checkstyle>().configureEach { task ->
            task.group = LifecycleBasePlugin.VERIFICATION_GROUP
            task.description = "Runs Checkstyle."

            val javaSources =
                project
                    .the<JavaPluginExtension>()
                    .sourceSets
                    .getByName("main")
                    .allSource
            task.source = javaSources.plus(quality.checkstyle.source.get()).asFileTree
            task.include(quality.checkstyle.include.get())
            task.exclude(excludeList.plus(quality.checkstyle.exclude.get()))

            task.reports {
                it.xml.required.set(true)
                it.html.required.set(true)
                it.sarif.required.set(true)
            }
        }
        project.tasks.named("check").configure { t ->
            t.dependsOn("checkstyleMain")
        }
    }

    private fun configureCodenarc() {
        if (!quality.codenarc.enabled.get() || !project.hasGroovySources()) return

        project.pluginManager.apply(CodeNarcPlugin::class.java)

        project.configure<CodeNarcExtension> {
            toolVersion = versionFromCatalogOrFail(libs, "codenarc")
            isIgnoreFailures = !quality.codenarc.failOnViolation.get()
        }
        project.tasks.withType<CodeNarc>().configureEach { task ->
            task.group = LifecycleBasePlugin.VERIFICATION_GROUP
            task.description = "Runs Codenarc."
            task.reports {
                it.xml.required.set(true)
                it.html.required.set(true)
            }
            task.configFile =
                resolveConfigFile(
                    toolName = "codenarc",
                    fileName =
                        if (task.name.contains("Test", ignoreCase = true)) {
                            "rules-test.groovy"
                        } else {
                            "rules.groovy"
                        },
                ).asFile

            task.source =
                project
                    .files(
                        "src/main/groovy",
                        "src/test/groovy",
                        "src/main/resources",
                    ).plus(quality.codenarc.source.get())
                    .asFileTree
                    .matching {
                        it.include("**/*.groovy")
                    }
        }

        project.tasks.named("check").configure { t ->
            t.dependsOn("codenarcMain")
        }
    }

    private fun configureSpotBugs() {
        if (!quality.spotbugs.enabled.get() || !project.hasJavaSources()) return

        project.pluginManager.apply(SpotBugsPlugin::class.java)

        project.configure<SpotBugsExtension> {
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

        project.tasks.named("check").configure { t ->
            t.dependsOn("spotbugsMain")
        }
    }

    private fun configurePmd() {
        if (!quality.pmd.enabled.get() || !project.hasJavaSources()) return

        project.pluginManager.apply(PmdPlugin::class.java)

        project.configure<PmdExtension> {
            toolVersion = versionFromCatalogOrFail(libs, "pmd")
            ruleSetFiles = project.files(resolveConfigFile("pmd", "pmd-ruleset.xml"))
            isConsoleOutput = quality.pmd.consoleOutput.get()
            isIgnoreFailures = !quality.pmd.failOnViolation.get()
        }
        project.tasks.withType<Pmd>().configureEach { task ->
            task.group = LifecycleBasePlugin.VERIFICATION_GROUP
            task.description = "Runs PMD."

            task.source = project.files("src/main/java").plus(quality.pmd.source.get()).asFileTree
            task.include(quality.pmd.include.get())
            task.exclude(excludeList.plus(quality.pmd.exclude.get()))

            task.reports { reports ->
                reports.xml.required.set(true)
                reports.html.required.set(true)
            }
        }
        project.tasks.named("check").configure { t ->
            t.dependsOn("pmdMain")
        }
    }

    private fun configureJacoco() {
        if (!quality.jacoco.enabled.get()) return

        project.pluginManager.apply(JacocoPlugin::class.java)

        project.configure<JacocoPluginExtension> {
            toolVersion = versionFromCatalogOrFail(libs, "jacoco")
        }

        val jacocoReportTasks = project.tasks.withType<JacocoReport>()
        val jacocoVerificationTasks = project.tasks.withType<JacocoCoverageVerification>()

        project.tasks.withType<Test>().configureEach { t ->
            t.finalizedBy(jacocoReportTasks)
        }

        jacocoReportTasks.configureEach { jacocoReport ->
            jacocoReport.reports { t ->
                t.xml.required.set(true)
                t.html.required.set(true)
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

        jacocoVerificationTasks.configureEach { t ->
            t.dependsOn(project.tasks.withType<JacocoReport>())
            t.violationRules { rules ->
                rules.rule { rule ->
                    rule.excludes =
                        listOf(
                            "**/generated/**",
                            "**/target/**",
                            "**/build/**",
                            "**/Messages.class",
                            "**/*Descriptor.class",
                            "**/jelly/**",
                            "**/tags/**",
                        ).plus(quality.jacoco.excludes.get())
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

        project.tasks.named("check").configure { t ->
            t.dependsOn("jacocoTestCoverageVerification")
        }
    }

    private fun configureDetekt() {
        if (!quality.detekt.enabled.get() || !project.hasKotlinSources()) return

        project.pluginManager.apply(DetektPlugin::class.java)

        project.configure<DetektExtension> {
            toolVersion = versionFromCatalogOrFail(libs, "detekt")
            autoCorrect = quality.detekt.autoCorrect.get()
            buildUponDefaultConfig = true
            isIgnoreFailures = !quality.detekt.failOnViolation.get()
            source.setFrom(quality.detekt.source.get())
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
        project.tasks.named("check").configure { t ->
            t.dependsOn("detekt")
        }
    }

    private fun configureSpotless() {
        if (!quality.spotless.enabled.get()) return

        project.pluginManager.apply(SpotlessPlugin::class.java)

        project.variantResolution("spotless")

        val headerFile = project.rootProject.file("config/spotless/license-header.txt")

        val commonExcludes =
            listOf(
                "**/build/**",
                "**/.gradle/**",
                "**/.idea/**",
                "**/node_modules/**",
                "**/.git/**",
                "**/generated/**",
                "**/out/**",
                "**/.gradle-test-kit/**",
            )

        project.configure<SpotlessExtension> {
            if (project.hasKotlinSources()) {
                kotlin { t ->
                    t.target(
                        "src/main/kotlin/**/*.kt",
                        "src/test/kotlin/**/*.kt",
                        "src/main/resources/**/*.kt",
                    )
                    t.targetExclude(commonExcludes)
                    t.ktlint(versionFromCatalogOrFail(libs, "ktlint"))
                    t.trimTrailingWhitespace()
                    t.endWithNewline()

                    if (headerFile.exists()) {
                        t.licenseHeaderFile(headerFile)
                    }
                }
                kotlinGradle { t ->
                    t.target(
                        "*.gradle.kts",
                        "**/*.gradle.kts",
                        "settings.gradle.kts",
                    )
                    t.targetExclude(commonExcludes + "**/gradle/**")
                    t.ktlint(versionFromCatalogOrFail(libs, "ktlint"))
                    t.trimTrailingWhitespace()
                    t.endWithNewline()

                    if (headerFile.exists()) {
                        t.licenseHeaderFile(
                            headerFile,
                            "(plugins|pluginManagement|import|buildscript|" +
                                "dependencyResolutionManagement|enableFeaturePreview|include|rootProject)",
                        )
                    }
                }
            }
            if (project.hasJavaSources()) {
                java { t ->
                    t.target(
                        "src/main/java/**/*.java",
                        "src/test/java/**/*.java",
                        "src/main/resources/**/*.java",
                    )
                    t.targetExclude(commonExcludes + "**/gradle/**")
                    t.palantirJavaFormat(versionFromCatalogOrFail(libs, "palantir-java"))
                    t.trimTrailingWhitespace()
                    t.endWithNewline()
                    t.removeUnusedImports()

                    if (headerFile.exists()) {
                        t.licenseHeaderFile(headerFile)
                    }
                }
            }
            if (project.hasGroovySources()) {
                groovy { t ->
                    t.target(
                        "src/main/groovy/**/*.groovy",
                        "src/test/groovy/**/*.groovy",
                        "src/main/resources/**/*.groovy",
                    )
                    t.targetExclude(commonExcludes + "**/gradle**")

                    t.greclipse()
                    t.trimTrailingWhitespace()
                    t.endWithNewline()

                    if (headerFile.exists()) {
                        t.licenseHeaderFile(headerFile)
                    }
                }
                groovyGradle { t ->
                    t.target(
                        "*.gradle",
                        "**/*.gradle",
                        "settings.gradle",
                    )
                    t.targetExclude(commonExcludes + "**/gradle/**")

                    t.greclipse()
                    t.trimTrailingWhitespace()
                    t.endWithNewline()

                    if (headerFile.exists()) {
                        t.licenseHeaderFile(
                            headerFile,
                            "(plugins|pluginManagement|import|buildscript|" +
                                "dependencyResolutionManagement|enableFeaturePreview|include|rootProject)",
                        )
                    }
                }
            }
            format("misc") { t ->
                t.target(
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
                    "*.sh",
                    "*.dockerfile",
                    "Dockerfile*",
                    ".env",
                    ".dockerignore",
                )
                t.targetExclude(commonExcludes)
                t.trimTrailingWhitespace()
                t.endWithNewline()
            }
        }

        project.tasks.named("check").configure { t ->
            t.dependsOn("spotlessCheck")
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

        project.tasks.named("check").configure { t ->
            t.dependsOn("dependencyCheckAnalyze")
        }
    }

    private fun configurePitMutation() {
        if (!quality.pitest.enabled.get() || !project.hasJavaSources()) return

        project.pluginManager.apply(PitestPlugin::class.java)

        project.configure<PitestPluginExtension> {
            threads.set(quality.pitest.threads)
            pitestVersion.set(versionFromCatalogOrFail(libs, "pit"))
            targetClasses.set(quality.pitest.targetClasses)
            excludedClasses.set(quality.pitest.excludedClasses)
            mutationThreshold.set(quality.pitest.mutationThreshold)
            outputFormats.set(quality.pitest.outputFormats)
            mutators.set(quality.pitest.mutators)
        }

        project.tasks.named("check").configure { t ->
            t.dependsOn("pitest")
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
        if (!quality.kover.enabled.get() || !project.hasKotlinSources()) return

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
        project.tasks.named("check").configure { t ->
            t.dependsOn("koverVerify")
        }
    }

    private fun configureEsLint() {
        if (!quality.eslint.enabled.get() || !project.isFrontendProject()) {
            return
        }

        project.pluginManager.apply(NodePlugin::class.java)

        project.tasks.register<NpmTask>("eslint") {
            group = LifecycleBasePlugin.VERIFICATION_GROUP
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
                configFile?.let { file -> argsList += listOf("--config", file) }
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

        project.tasks.named("check").configure { t ->
            t.dependsOn("eslint")
        }
    }

    private fun configureDokka() {
        if (!quality.dokka.enabled.get() || !project.hasKotlinSources()) return

        project.pluginManager.apply(DokkaPlugin::class.java)
    }

    private fun configureCpd() {
        if (!quality.cpd.enabled.get()) return

        project.pluginManager.apply(CpdPlugin::class.java)

        project.configure<CpdExtension> {
            toolVersion = versionFromCatalogOrFail(libs, "cpd")
            isIgnoreFailures = !quality.cpd.failOnViolation.get()
            minimumTokenCount = quality.cpd.minimumTokenCount.get()
        }
        project.tasks.withType<Cpd>().configureEach { task ->
            task.group = LifecycleBasePlugin.VERIFICATION_GROUP
            task.description = "Runs CPD."

            task.source =
                project
                    .files(
                        "src/main/java",
                        "src/main/groovy",
                    ).plus(quality.cpd.source.get())
                    .asFileTree
                    .matching {
                        it.include("**/*.java", "**/*.groovy")
                    }

            task.reports {
                it.xml.required.set(true)
                it.text.required.set(true)
            }
        }

        project.tasks.named("check").configure { t ->
            t.dependsOn("cpdCheck")
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

private fun Project.hasJavaSources(): Boolean = fileTree("src").matching { it.include("**/*.java") }.files.isNotEmpty()

private fun Project.hasKotlinSources(): Boolean = fileTree("src").matching { it.include("**/*.kotlin") }.files.isNotEmpty()

private fun Project.hasGroovySources(): Boolean = fileTree("src").matching { it.include("**/*.groovy") }.files.isNotEmpty()

private fun Project.isFrontendProject(): Boolean =
    listOf("package.json", "yarn.lock", "pnpm-lock.yaml").any { file(it).exists() } ||
        listOf("src/main/js", "src/main/ts", "src/main/webapp").any { file(it).isDirectory }

private fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}
