package internal

import com.diffplug.gradle.spotless.SpotlessExtension
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.gradle.node.npm.task.NpmTask
import com.github.spotbugs.snom.SpotBugsExtension
import extensions.QualityExtension
import info.solidsoft.gradle.pitest.PitestPluginExtension
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.plugins.quality.Pmd
import org.gradle.api.plugins.quality.PmdExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.owasp.dependencycheck.gradle.extension.DependencyCheckExtension

@Suppress("TooManyFunctions")
public class QualityManager(
    private val project: Project,
    private val qualityExtension: QualityExtension,
) {
    public fun apply() {
        applyQualityPlugins()
        configure()
        configureCheckTask()
    }

    private fun configure() {
        configureCheckstyle()
        configureSpotBugs()
        configurePmd()
        configureJacoco()
        configureDetekt()
        configureSpotless()
        configureOwaspDependencyCheck()
        configurePitMutation()
        configureGradleVersionPlugin()
        configureKoverExtension()
        configureEsLint()
        configureDokka()
    }

    private fun applyQualityPlugins() {
        listOf(
            qualityExtension.checkstyle.enabled.get() to "checkstyle",
            qualityExtension.spotless.enabled.get() to "com.diffplug.gradle.spotless",
            qualityExtension.pmd.enabled.get() to "pmd",
            qualityExtension.jacoco.enabled.get() to "jacoco",
            qualityExtension.detekt.enabled.get() to "io.gitlab.arturbosch.detekt",
            qualityExtension.spotbugs.enabled.get() to "com.github.spotbugs",
            qualityExtension.owaspDependencyCheck.enabled.get() to "org.owasp.dependencycheck",
            qualityExtension.versions.enabled.get() to "com.github.ben-manes.versions",
            qualityExtension.kover.enabled.get() to "org.jetbrains.kotlinx.kover",
            qualityExtension.pitest.enabled.get() to "info.solidsoft.gradle.pitest",
            (qualityExtension.eslint.enabled.get() && hasFrontendCode()) to "com.github.node-gradle.node",
            qualityExtension.dokka.enabled.get() to "org.jetbrains.dokka",
        ).forEach { (enabled, pluginId) ->
            if (enabled) {
                project.pluginManager.apply(pluginId)
            }
        }
    }

    private fun configureCheckstyle() {
        val checkstyle = qualityExtension.checkstyle
        if (!checkstyle.enabled.get()) return
        project.configure<CheckstyleExtension> {
            toolVersion = checkstyle.toolVersion.get()
            checkstyle.configFile.orNull?.let {
                configFile = it.asFile
            }
            isIgnoreFailures = !checkstyle.failOnViolation.get()
        }
        project.tasks.withType<Checkstyle>().configureEach { checkstyleTask ->
            checkstyleTask.reports {
                it.xml.required.set(true)
                it.html.required.set(true)
                it.sarif.required.set(true)
            }
        }
    }

    private fun configureSpotBugs() {
        if (!qualityExtension.spotbugs.enabled.get()) return

        project.configure<SpotBugsExtension> {
            toolVersion.set(qualityExtension.spotbugs.toolVersion.get())
            effort.set(qualityExtension.spotbugs.effortLevel.get())
            reportLevel.set(qualityExtension.spotbugs.reportLevel.get())
            excludeFilter.set(qualityExtension.spotbugs.excludeFilterFile.orNull)
        }
    }

    private fun configurePmd() {
        if (!qualityExtension.pmd.enabled.get()) return

        project.configure<PmdExtension> {
            toolVersion = qualityExtension.pmd.toolVersion.get()
            qualityExtension.pmd.ruleSetFiles.orNull?.let {
                ruleSetFiles = project.files(it.asFile)
            }
            isConsoleOutput = qualityExtension.pmd.consoleOutput.get()
            isIgnoreFailures = !qualityExtension.pmd.failOnViolation.get()
        }
        project.tasks.withType<Pmd>().configureEach { pmd ->
            pmd.reports {
                it.xml.required.set(true)
                it.html.required.set(true)
            }
        }
        if (qualityExtension.pmd.enableCPD.get()) {
            project.tasks.register("cpdCheck") {
                it.group = "verification"
                it.description = "Run CPD copy-paste-detection"
                it.dependsOn(project.tasks.withType<Pmd>())
            }
        }
    }

    private fun configureJacoco() {
        val jacocoConfig = qualityExtension.jacoco
        if (!jacocoConfig.enabled.get()) return

        project.configure<JacocoPluginExtension> {
            toolVersion = jacocoConfig.toolVersion.get()
        }
        project.tasks.withType<Test>().configureEach { t ->
            t.finalizedBy(project.tasks.withType<JacocoReport>())
        }
        project.tasks.withType<JacocoReport>().configureEach { jacocoReport ->
            jacocoReport.dependsOn(project.tasks.withType<Test>())
            jacocoReport.reports {
                it.xml.required.set(true)
                it.html.required.set(true)
                it.csv.required.set(false)
            }
        }
        project.tasks.withType<JacocoCoverageVerification>().configureEach { t ->
            t.violationRules { rules ->
                rules.rule { rule ->
                    rule.excludes.addAll(jacocoConfig.excludes.get())
                    rule.limit {
                        it.counter = "LINE"
                        it.value = "COVEREDRATIO"
                        it.minimum = jacocoConfig.minimumCodeCoverage.get().toBigDecimal()
                    }
                }
            }
        }
    }

    private fun configureDetekt() {
        if (!qualityExtension.detekt.enabled.get()) return

        project.configure<DetektExtension> {
            toolVersion = qualityExtension.detekt.toolVersion.get()
            autoCorrect = qualityExtension.detekt.autoCorrect.get()
            buildUponDefaultConfig = true
            isIgnoreFailures = !qualityExtension.detekt.failOnViolation.get()
            source.setFrom(qualityExtension.detekt.source)
            config.setFrom(listOfNotNull(qualityExtension.detekt.configFile.asFile.orNull))
            baseline =
                qualityExtension.detekt.baseline.orNull
                    ?.asFile
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
        if (!qualityExtension.spotless.enabled.get()) return

        project.configure<SpotlessExtension> {
            val ktlintVersion = qualityExtension.spotless.ktlintVersion.get()
            val googleJavaFormat = qualityExtension.spotless.googleJavaFormatVersion.get()

            kotlin {
                it.target("**/*.kt")
                it.targetExclude("**/build/**", "bin/**")
                it.ktlint(ktlintVersion)
                it.trimTrailingWhitespace()
                it.endWithNewline()
            }
            kotlinGradle {
                it.target("*.gradle.kts", "**/*.gradle.kts")
                it.ktlint(ktlintVersion)
                it.trimTrailingWhitespace()
                it.endWithNewline()
            }
            java {
                it.target("**/*.java")
                it.googleJavaFormat(googleJavaFormat)
                it.trimTrailingWhitespace()
                it.endWithNewline()
                it.targetExclude("**/generated/**", "**/build/**")
            }
            format("misc") {
                it.target(
                    "*.md",
                    ".gitignore",
                    "*.properties",
                    "*.yml",
                    "*.yaml",
                    "*.json",
                    ".editorconfig",
                )
                it.targetExclude("**/build/**", "**/.gradle/**", "**/.idea/**")
                it.trimTrailingWhitespace()
                it.endWithNewline()
            }
        }
    }

    private fun configureOwaspDependencyCheck() {
        val owasp = qualityExtension.owaspDependencyCheck

        if (!owasp.enabled.get()) return

        project.configure<DependencyCheckExtension> {
            failBuildOnCVSS = owasp.failOnCvss.get()
            formats = owasp.formats.get()
            suppressionFiles =
                owasp.suppressionFiles
                    .get()
                    .map { it.asFile.absolutePath }
            owasp.outputDirectory
                .get()
                .asFile.absolutePath
            data {
                it.directory =
                    owasp.dataDirectory
                        .get()
                        .asFile.absolutePath
            }
            scanConfigurations = owasp.scanConfigurations.get()
        }
    }

    private fun configurePitMutation() {
        val pit = qualityExtension.pitest
        if (!pit.enabled.get()) return

        project.configure<PitestPluginExtension> {
            threads.set(pit.threads)
            pitestVersion.set(pit.pitVersion.get())
            targetClasses.set(pit.targetClasses)
            excludedClasses.set(pit.excludedClasses)
            mutationThreshold.set(pit.mutationThreshold)
            outputFormats.set(pit.outputFormats)
            mutators.set(pit.mutators)
        }
    }

    private fun configureGradleVersionPlugin() {
        val versions = qualityExtension.versions
        if (!versions.enabled.get()) return

        project.tasks.withType<DependencyUpdatesTask>().configureEach { t ->
            t.rejectVersionIf {
                isNonStable(it.candidate.version) && !isNonStable(it.currentVersion)
            }
        }
    }

    private fun isNonStable(version: String): Boolean {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        return isStable.not()
    }

    private fun configureKoverExtension() {
        val kover = qualityExtension.kover
        if (!kover.enabled.get()) return

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
                            it.minValue.set(qualityExtension.kover.coverageThreshold)
                        }
                    }
                }
            }
        }
    }

    private fun configureEsLint() {
        val esLint = qualityExtension.eslint

        if (!esLint.enabled.get()) return

        project.tasks.register<NpmTask>("eslint") {
            group = "Verification"
            description = "Run ESLint on frontend sources."

            val configFile =
                esLint.configFile.orNull
                    ?.asFile
                    ?.absolutePath

            val baseArgs = mutableListOf("run", "lint")

            if (esLint.autofix.get()) {
                baseArgs.add("--fix")
            }

            configFile?.let {
                baseArgs.addAll(listOf("--config", it))
            }

            args.set(baseArgs)
        }
    }

    private fun configureDokka() {
        val dokka = qualityExtension.dokka

        if (!dokka.enabled.get()) return

        val formats = qualityExtension.dokka.formats.get()
        val dokkaTasks = formats.map { "dokka${it.capitalized()}" }

        project.tasks.register("generateDocs") {
            it.group = "Documentation"
            it.description = "Generate API documentation in multiple formats: ${formats.joinToString { ", " }}"
            it.dependsOn(dokkaTasks)
        }
    }

    private fun hasFrontendCode(): Boolean {
        if (project.file("package.json").exists()) return true

        val frontendDirs = listOf("src/main/js", "src/main/ts")
        return frontendDirs.any { project.fileTree(it).files.isNotEmpty() }
    }

    private fun configureCheckTask() {
        project.tasks.named("check").configure {
            listOf(
                qualityExtension.checkstyle.enabled.get() to "checkstyleMain",
                qualityExtension.spotbugs.enabled.get() to "spotbugsMain",
                qualityExtension.pmd.enabled.get() to "pmdMain",
                qualityExtension.spotless.enabled.get() to "spotlessCheck",
                qualityExtension.detekt.enabled.get() to "detekt",
                qualityExtension.owaspDependencyCheck.enabled.get() to "dependencyCheckAnalyze",
                qualityExtension.kover.enabled.get() to "koverVerify",
                qualityExtension.pitest.enabled.get() to "pitest",
                (qualityExtension.eslint.enabled.get() && project.tasks.findByName("eslint") != null) to
                    "eslint",
                qualityExtension.jacoco.enabled.get() to "jacocoTestCoverageVerification",
            ).forEach { (enabled, path) ->
                {
                    if (enabled) {
                        it.dependsOn(path)
                    }
                }
            }
        }
    }
}
