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
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.plugins.quality.CodeNarc
import org.gradle.api.plugins.quality.CodeNarcExtension
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
        configureCheckTask()
    }

    private fun configureCheckstyle() {
        if (!qualityExtension.checkstyle.enabled.getOrElse(false)) return

        project.pluginManager.apply("checkstyle")

        project.configure<CheckstyleExtension> {
            toolVersion = qualityExtension.checkstyle.toolVersion.get()
            configFile =
                qualityExtension.checkstyle.configFile
                    .get()
                    .asFile
            isIgnoreFailures = !qualityExtension.checkstyle.failOnViolation.get()
        }
        project.tasks.withType<Checkstyle>().configureEach { checkstyleTask ->
            checkstyleTask.reports {
                it.xml.required.set(true)
                it.html.required.set(true)
                it.sarif.required.set(true)
            }
        }
    }

    private fun configureCodenarc() {
        if (!qualityExtension.codenarc.enabled.getOrElse(false)) return

        project.pluginManager.apply("codenarc")

        project.configure<CodeNarcExtension> {
            toolVersion = qualityExtension.codenarc.toolVersion.get()
            qualityExtension.codenarc.configFile.orNull?.let {
                configFile = it.asFile
            }
            isIgnoreFailures = !qualityExtension.codenarc.failOnViolation.get()
        }
        project.tasks.withType<CodeNarc>().configureEach { task ->
            task.reports {
                it.xml.required.set(true)
                it.html.required.set(true)
            }
        }
    }

    private fun configureSpotBugs() {
        if (!qualityExtension.spotbugs.enabled.getOrElse(false)) return

        project.pluginManager.apply("com.github.spotbugs")

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
        if (!qualityExtension.pmd.enabled.getOrElse(false)) return

        project.pluginManager.apply("pmd")

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
        if (qualityExtension.pmd.enableCPD.getOrElse(false)) {
            project.tasks.register("cpdCheck") {
                it.group = "verification"
                it.description = "Run CPD copy-paste-detection"
                it.dependsOn(project.tasks.withType<Pmd>())
            }
        }
    }

    private fun configureJacoco() {
        val hasJava = project.plugins.hasPlugin("java") || project.plugins.hasPlugin("java-library")
        if (!qualityExtension.jacoco.enabled.getOrElse(false) || !hasJava) return

        project.pluginManager.apply("jacoco")

        project.configure<JacocoPluginExtension> {
            toolVersion = qualityExtension.jacoco.toolVersion.get()
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

            val allExcludes = qualityExtension.jacoco.excludes.get()
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
                    rule.excludes = qualityExtension.jacoco.excludes.get()
                    rule.limit {
                        it.counter = "LINE"
                        it.value = "COVEREDRATIO"
                        it.minimum =
                            qualityExtension.jacoco.minimumCodeCoverage
                                .get()
                                .toBigDecimal()
                    }
                }
            }
        }
    }

    private fun configureDetekt() {
        if (!qualityExtension.detekt.enabled.getOrElse(false)) return

        project.pluginManager.apply("io.gitlab.arturbosch.detekt")

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
        if (!qualityExtension.spotless.enabled.get()) return

        project.pluginManager.apply("com.diffplug.spotless")

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
                it.googleJavaFormat()
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
        if (!qualityExtension.owaspDependencyCheck.enabled.getOrElse(true)) return

        project.pluginManager.apply("org.owasp.dependencycheck")

        project.configure<DependencyCheckExtension> {
            failBuildOnCVSS = qualityExtension.owaspDependencyCheck.failOnCvss.get()
            formats = qualityExtension.owaspDependencyCheck.formats.get()
            suppressionFiles =
                qualityExtension.owaspDependencyCheck.suppressionFiles
                    .get()
                    .map { it.asFile.absolutePath }
            qualityExtension.owaspDependencyCheck.outputDirectory
                .get()
                .asFile.absolutePath
            data {
                it.directory =
                    qualityExtension.owaspDependencyCheck.dataDirectory
                        .get()
                        .asFile.absolutePath
            }
            scanConfigurations = qualityExtension.owaspDependencyCheck.scanConfigurations.get()
        }
    }

    private fun configurePitMutation() {
        if (!qualityExtension.pitest.enabled.getOrElse(false)) return

        project.pluginManager.apply("info.solidsoft.pitest")

        project.configure<PitestPluginExtension> {
            threads.set(qualityExtension.pitest.threads)
            pitestVersion.set(qualityExtension.pitest.pitVersion.get())
            targetClasses.set(qualityExtension.pitest.targetClasses)
            excludedClasses.set(qualityExtension.pitest.excludedClasses)
            mutationThreshold.set(qualityExtension.pitest.mutationThreshold)
            outputFormats.set(qualityExtension.pitest.outputFormats)
            mutators.set(qualityExtension.pitest.mutators)
        }
    }

    private fun configureGradleVersionPlugin() {
        if (!qualityExtension.versions.enabled.getOrElse(false)) return

        project.pluginManager.apply("com.github.ben-manes.versions")

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
        if (!qualityExtension.kover.enabled.getOrElse(false)) return

        project.pluginManager.apply("org.jetbrains.kotlinx.kover")

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
        if (!qualityExtension.eslint.enabled.getOrElse(false) || !hasFrontendCode()) return

        project.pluginManager.apply("com.github.node-gradle.node")

        project.tasks.register<NpmTask>("eslint") {
            group = "Verification"
            description = "Run ESLint on frontend sources."

            val configFile =
                qualityExtension.eslint.configFile.orNull
                    ?.asFile
                    ?.absolutePath

            val baseArgs = mutableListOf("run", "lint")

            if (qualityExtension.eslint.autofix.get()) {
                baseArgs.add("--fix")
            }

            configFile?.let {
                baseArgs.addAll(listOf("--config", it))
            }

            args.set(baseArgs)
        }
    }

    private fun configureDokka() {
        if (!qualityExtension.dokka.enabled.get()) return

        project.pluginManager.apply("org.jetbrains.dokka")

        project.tasks.named("dokkaHtml").configure { task ->
            task.outputs.dir(qualityExtension.dokka.outputDirectory)
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
                qualityExtension.checkstyle.enabled.getOrElse(false) to "checkstyleMain",
                qualityExtension.codenarc.enabled.getOrElse(false) to "codenarcMain",
                qualityExtension.spotbugs.enabled.getOrElse(false) to "spotbugsMain",
                qualityExtension.pmd.enabled.getOrElse(false) to "pmdMain",
                qualityExtension.spotless.enabled.getOrElse(false) to "spotlessCheck",
                qualityExtension.detekt.enabled.getOrElse(false) to "detekt",
                qualityExtension.owaspDependencyCheck.enabled.getOrElse(false) to "dependencyCheckAnalyze",
                qualityExtension.kover.enabled.getOrElse(false) to "koverVerify",
                qualityExtension.pitest.enabled.getOrElse(false) to "pitest",
                (qualityExtension.eslint.enabled.getOrElse(false) && project.tasks.findByName("eslint") != null) to
                    "eslint",
                (qualityExtension.jacoco.enabled.getOrElse(false)) to "jacocoTestCoverageVerification",
            ).forEach { (enabled, path) ->
                if (enabled) {
                    it.dependsOn(path)
                }
            }
        }
    }
}
