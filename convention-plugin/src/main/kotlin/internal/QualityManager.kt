package internal

import com.diffplug.gradle.spotless.SpotlessExtension
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.gradle.node.npm.task.NpmTask
import com.github.spotbugs.snom.SpotBugsExtension
import com.github.spotbugs.snom.SpotBugsTask
import extensions.QualityExtension
import info.solidsoft.gradle.pitest.PitestPluginExtension
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.plugins.quality.Pmd
import org.gradle.api.plugins.quality.PmdExtension
import org.gradle.api.tasks.testing.Test
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
        val hasJava = project.plugins.hasPlugin("java") || project.plugins.hasPlugin("java-library")

        val pluginIds =
            buildList {
                addIfEnabled(qualityExtension.checkstyle.enabled.get(), "checkstyle")
                addIfEnabled(qualityExtension.spotless.enabled.get(), "com.diffplug.spotless")
                addIfEnabled(qualityExtension.pmd.enabled.get(), "pmd")
                addIfEnabled(qualityExtension.jacoco.enabled.get() && hasJava, "jacoco")
                addIfEnabled(qualityExtension.detekt.enabled.get(), "io.gitlab.arturbosch.detekt")
                addIfEnabled(qualityExtension.spotbugs.enabled.get(), "com.github.spotbugs")
                addIfEnabled(qualityExtension.owaspDependencyCheck.enabled.get(), "org.owasp.dependencycheck")
                addIfEnabled(qualityExtension.versions.enabled.get(), "com.github.ben-manes.versions")
                addIfEnabled(qualityExtension.kover.enabled.get(), "org.jetbrains.kotlinx.kover")
                addIfEnabled(qualityExtension.pitest.enabled.get(), "info.solidsoft.pitest")
                addIfEnabled(
                    qualityExtension.dokka.enabled.orNull == true,
                    "org.jetbrains.dokka",
                )
                if (qualityExtension.eslint.enabled.orNull == true &&
                    hasFrontendCode()
                ) {
                    add("com.github.node-gradle.node")
                }
            }

        pluginIds.forEach { pluginId ->
            project.pluginManager.apply(pluginId)
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

        project.tasks.withType<SpotBugsTask>().configureEach {
            it.reports.create("html") { report ->
                report.required.set(true)
            }
            it.reports.create("sarif") { report ->
                report.required.set(true)
            }
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
        val hasJava = project.plugins.hasPlugin("java") || project.plugins.hasPlugin("java-library")
        val jacocoConfig = qualityExtension.jacoco
        if (!jacocoConfig.enabled.get() || !hasJava) return

        project.configure<JacocoPluginExtension> {
            toolVersion = jacocoConfig.toolVersion.get()
        }
        project.tasks.withType<Test>().configureEach { t ->
            t.finalizedBy("jacocoTestReport")
        }
        project.tasks.withType<JacocoReport>().configureEach { jacocoReport ->
            jacocoReport.dependsOn(project.tasks.withType<Test>())
            jacocoReport.reports {
                it.xml.required.set(true)
                it.html.required.set(true)
                it.csv.required.set(false)
            }

            val allExcludes = jacocoConfig.excludes.get()
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
                    rule.excludes = jacocoConfig.excludes.get()
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

        if (!esLint.enabled.get() || !hasFrontendCode()) return

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

        project.tasks.named("dokkaHtml").configure { task ->
            task.outputs.dir(dokka.outputDirectory)
        }
    }

    private fun hasFrontendCode(): Boolean {
        if (project.file("package.json").exists()) return true

        val frontendDirs = listOf("src/main/js", "src/main/ts")
        return frontendDirs.any { project.fileTree(it).files.isNotEmpty() }
    }

    private fun configureCheckTask() {
        project.tasks.named("check").configure {
            val hasJava = project.plugins.hasPlugin(JavaPlugin::class.java)
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
                (qualityExtension.jacoco.enabled.get() && hasJava) to "jacocoTestCoverageVerification",
            ).forEach { (enabled, path) ->
                if (enabled) {
                    it.dependsOn(path)
                }
            }
        }
    }
}

private fun MutableList<String>.addIfEnabled(
    enabled: Boolean,
    pluginId: String,
) {
    if (enabled) add(pluginId)
}
