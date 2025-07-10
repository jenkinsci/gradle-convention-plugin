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
package extensions

import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import constants.ConfigurationConstants
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty
import javax.inject.Inject

@Suppress("TooManyFunctions")
public abstract class QualityExtension
    @Inject
    constructor(
        project: Project,
        libs: VersionCatalog,
    ) {
        public val checkstyle: CheckstyleExtension =
            project.objects.newInstance(CheckstyleExtension::class.java, libs, project)
        public val codenarc: CodenarcExtension =
            project.objects.newInstance(CodenarcExtension::class.java, project, libs)
        public val spotbugs: SpotbugsExtension =
            project.objects.newInstance(
                SpotbugsExtension::class.java,
                project,
                libs,
            )
        public val pmd: PmdExtension = project.objects.newInstance(PmdExtension::class.java, project, libs)
        public val jacoco: JacocoExtension =
            project.objects.newInstance(
                JacocoExtension::class.java,
                project,
                libs,
            )
        public val detekt: DetektExtension =
            project.objects.newInstance(
                DetektExtension::class.java,
                project,
                libs,
            )
        public val spotless: SpotlessExtension =
            project.objects.newInstance(
                SpotlessExtension::class.java,
                project,
                libs,
            )
        public val owaspDependencyCheck: OwaspDependencyCheckExtension =
            project.objects.newInstance(
                OwaspDependencyCheckExtension::class.java,
                project,
            )
        public val versions: GradleVersionExtension =
            project.objects.newInstance(GradleVersionExtension::class.java, project)
        public val pitest: PitestExtension = project.objects.newInstance(PitestExtension::class.java, project, libs)
        public val kover: KoverExtension = project.objects.newInstance(KoverExtension::class.java, project)
        public val eslint: EslintExtension = project.objects.newInstance(EslintExtension::class.java, project)
        public val dokka: DokkaExtension = project.objects.newInstance(DokkaExtension::class.java, project)

        public fun checkstyle(action: CheckstyleExtension.() -> Unit): Unit = action(checkstyle)

        public fun codenarc(action: CodenarcExtension.() -> Unit): Unit = action(codenarc)

        public fun spotbugs(action: SpotbugsExtension.() -> Unit): Unit = action(spotbugs)

        public fun pmd(action: PmdExtension.() -> Unit): Unit = action(pmd)

        public fun jacoco(action: JacocoExtension.() -> Unit): Unit = action(jacoco)

        public fun detekt(action: DetektExtension.() -> Unit): Unit = action(detekt)

        public fun spotless(action: SpotlessExtension.() -> Unit): Unit = action(spotless)

        public fun owaspDependencyCheck(action: OwaspDependencyCheckExtension.() -> Unit): Unit =
            action(owaspDependencyCheck)

        public fun pitest(action: PitestExtension.() -> Unit): Unit = action(pitest)

        public fun versions(action: GradleVersionExtension.() -> Unit): Unit = action(versions)

        public fun kover(action: KoverExtension.() -> Unit): Unit = action(kover)

        public fun eslint(action: EslintExtension.() -> Unit): Unit = action(eslint)

        public fun dokka(action: DokkaExtension.() -> Unit): Unit = action(dokka)

        public companion object {
            public const val DEFAULT_CODE_COVERAGE_THRESHOLD: Double = 0.8
            public const val DEFAULT_OWASP_THRESHOLD: Float = 7.0f
            public const val DEFAULT_MUTATION_THRESHOLD: Int = 85
            public const val DEFAULT_KOVER_THRESHOLD: Int = 80
            public const val DEFAULT_THREADS: Int = 4
        }
    }

private fun <T : Any> gradleProperty(
    project: Project,
    key: String,
    converter: (String) -> T,
): Provider<T> = project.providers.gradleProperty(key).map(converter)

private fun gradleProperty(
    project: Project,
    key: String,
) = project.providers.gradleProperty(key)

public abstract class CheckstyleExtension
    @Inject
    constructor(
        libs: VersionCatalog,
        project: Project,
    ) {
        public val enabled: Property<Boolean> =
            project.objects.property<Boolean>().convention(
                gradleProperty(
                    project,
                    ConfigurationConstants.CHECKSTYLE_ENABLED,
                    String::toBoolean,
                ).orElse(true),
            )
        public val toolVersion: Property<String> =
            project.objects.property<String>().convention(
                gradleProperty(
                    project,
                    ConfigurationConstants.CHECKSTYLE_VERSION,
                ).orElse(libs.findVersion("checkstyle").get().requiredVersion),
            )
        public val failOnViolation: Property<Boolean> = project.objects.property<Boolean>().convention(true)
        public val configFile: RegularFileProperty = project.objects.fileProperty()
    }

public abstract class SpotbugsExtension
    @Inject
    constructor(
        project: Project,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            project.objects.property<Boolean>().convention(
                gradleProperty(
                    project,
                    ConfigurationConstants.SPOTBUGS_ENABLED,
                    String::toBoolean,
                ).orElse(true),
            )
        public val toolVersion: Property<String> =
            project.objects.property<String>().convention(
                gradleProperty(
                    project,
                    ConfigurationConstants.SPOTBUGS_VERSION,
                ).orElse(libs.findVersion("spotbugsTool").get().requiredVersion),
            )
        public val effortLevel: Property<Effort> = project.objects.property<Effort>().convention(Effort.MAX)
        public val reportLevel: Property<Confidence> = project.objects.property<Confidence>().convention(Confidence.LOW)
        public val excludeFilterFile: RegularFileProperty = project.objects.fileProperty()
    }

public abstract class PmdExtension
    @Inject
    constructor(
        project: Project,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            project.objects.property<Boolean>().convention(
                gradleProperty(
                    project,
                    ConfigurationConstants.PMD_ENABLED,
                    String::toBoolean,
                ).orElse(true),
            )
        public val toolVersion: Property<String> =
            project.objects.property<String>().convention(
                gradleProperty(project, ConfigurationConstants.PMD_VERSION).orElse(
                    libs.findVersion("pmd").get().requiredVersion,
                ),
            )
        public val enableCPD: Property<Boolean> = project.objects.property<Boolean>().convention(false)
        public val consoleOutput: Property<Boolean> = project.objects.property<Boolean>().convention(true)
        public val failOnViolation: Property<Boolean> = project.objects.property<Boolean>().convention(true)
        public val ruleSetFiles: RegularFileProperty = project.objects.fileProperty()
    }

public abstract class JacocoExtension
    @Inject
    constructor(
        project: Project,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            project.objects.property<Boolean>().convention(
                gradleProperty(
                    project,
                    ConfigurationConstants.JACOCO_ENABLED,
                    String::toBoolean,
                ).orElse(true),
            )
        public val toolVersion: Property<String> =
            project.objects.property<String>().convention(
                gradleProperty(project, ConfigurationConstants.JACOCO_VERSION).orElse(
                    libs.findVersion("jacoco").get().requiredVersion,
                ),
            )
        public val minimumCodeCoverage: Property<Double> =
            project.objects.property<Double>().convention(
                QualityExtension.DEFAULT_CODE_COVERAGE_THRESHOLD,
            )
        public val excludes: ListProperty<String> =
            project.objects.listProperty<String>().convention(
                listOf(
                    "**/generated/**",
                    "**/Messages.class",
                    "**/*Descriptor.class",
                    "**/*Jelly.class",
                    "**/tags/**",
                ),
            )
    }

public abstract class DetektExtension
    @Inject
    constructor(
        project: Project,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            project.objects.property<Boolean>().convention(
                gradleProperty(
                    project,
                    ConfigurationConstants.DETEKT_ENABLED,
                    String::toBoolean,
                ).orElse(true),
            )
        public val toolVersion: Property<String> =
            project.objects.property<String>().convention(
                gradleProperty(project, ConfigurationConstants.DETEKT_VERSION).orElse(
                    libs.findVersion("detekt").get().requiredVersion,
                ),
            )
        public val autoCorrect: Property<Boolean> = project.objects.property<Boolean>().convention(false)
        public val failOnViolation: Property<Boolean> = project.objects.property<Boolean>().convention(false)
        public val source: ListProperty<String> =
            project.objects.listProperty<String>().convention(
                listOf("src/main/java", "src/main/kotlin"),
            )
        public val configFile: RegularFileProperty = project.objects.fileProperty()
        public val baseline: RegularFileProperty = project.objects.fileProperty()
    }

public abstract class SpotlessExtension
    @Inject
    constructor(
        project: Project,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            project.objects.property<Boolean>().convention(
                gradleProperty(
                    project,
                    ConfigurationConstants.SPOTLESS_ENABLED,
                    String::toBoolean,
                ).orElse(true),
            )
    }

public abstract class OwaspDependencyCheckExtension
    @Inject
    constructor(
        project: Project,
    ) {
        public val enabled: Property<Boolean> =
            project.objects.property<Boolean>().convention(
                gradleProperty(
                    project,
                    ConfigurationConstants.OWASP_ENABLED,
                    String::toBoolean,
                ).orElse(false),
            )
        public val failOnCvss: Property<Float> =
            project.objects.property<Float>().convention(QualityExtension.DEFAULT_OWASP_THRESHOLD)
        public val formats: ListProperty<String> =
            project.objects.listProperty<String>().convention(
                setOf("XML", "HTML", "SARIF"),
            )
        public val dataDirectory: DirectoryProperty =
            project.objects.directoryProperty().convention(
                project.layout.projectDirectory.dir(".gradle/dependency-check-data"),
            )
        public val outputDirectory: DirectoryProperty =
            project.objects.directoryProperty().convention(
                project.layout.buildDirectory.dir("reports/dependency-check"),
            )
        public val suppressionFiles: ListProperty<RegularFile> =
            project.objects
                .listProperty<RegularFile>()
                .convention(
                    emptyList(),
                )
        public val scanConfigurations: ListProperty<String> =
            project.objects.listProperty<String>().convention(
                listOf("runtimeClasspath", "compileClasspath"),
            )
    }

public abstract class PitestExtension
    @Inject
    constructor(
        project: Project,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            project.objects.property<Boolean>().convention(
                gradleProperty(
                    project,
                    ConfigurationConstants.PITEST_ENABLED,
                    String::toBoolean,
                ).orElse(false),
            )
        public val pitVersion: Property<String> =
            project.objects.property<String>().convention(
                gradleProperty(project, ConfigurationConstants.PITEST_VERSION).orElse(
                    libs.findVersion("pit").get().requiredVersion,
                ),
            )
        public val threads: Property<Int> = project.objects.property<Int>().convention(QualityExtension.DEFAULT_THREADS)
        public val targetClasses: ListProperty<String> = project.objects.listProperty<String>().convention(listOf("*"))
        public val excludedClasses: ListProperty<String> =
            project.objects.listProperty<String>().convention(listOf("*Test*"))
        public val mutationThreshold: Property<Int> =
            project.objects.property<Int>().convention(
                gradleProperty(
                    project,
                    ConfigurationConstants.PITEST_MUTATION_THRESHOLD,
                    String::toInt,
                ).orElse(QualityExtension.DEFAULT_MUTATION_THRESHOLD),
            )
        public val outputFormats: ListProperty<String> =
            project.objects.listProperty<String>().convention(
                listOf("XML", "HTML"),
            )
        public val mutators: SetProperty<String> = project.objects.setProperty<String>().convention(setOf("DEFAULT"))
    }

public abstract class GradleVersionExtension
    @Inject
    constructor(
        project: Project,
    ) {
        public val enabled: Property<Boolean> = project.objects.property<Boolean>().convention(true)
    }

public abstract class KoverExtension
    @Inject
    constructor(
        project: Project,
    ) {
        public val enabled: Property<Boolean> =
            project.objects.property<Boolean>().convention(
                gradleProperty(
                    project,
                    ConfigurationConstants.KOVER_ENABLED,
                    String::toBoolean,
                ).orElse(true),
            )
        public val coverageThreshold: Property<Int> =
            project.objects.property<Int>().convention(QualityExtension.DEFAULT_KOVER_THRESHOLD)
    }

public abstract class EslintExtension
    @Inject
    constructor(
        project: Project,
    ) {
        public val enabled: Property<Boolean> =
            project.objects.property<Boolean>().convention(
                gradleProperty(
                    project,
                    ConfigurationConstants.ESLINT_ENABLED,
                    String::toBoolean,
                ).orElse(true),
            )
        public val autofix: Property<Boolean> = project.objects.property<Boolean>().convention(false)
        public val configFile: RegularFileProperty = project.objects.fileProperty()
    }

public abstract class DokkaExtension
    @Inject
    constructor(
        project: Project,
    ) {
        public val enabled: Property<Boolean> =
            project.objects.property<Boolean>().convention(
                gradleProperty(
                    project,
                    ConfigurationConstants.DOKKA_ENABLED,
                    String::toBoolean,
                ).orElse(true),
            )
        public val outputDirectory: DirectoryProperty =
            project.objects.directoryProperty().convention(project.layout.buildDirectory.dir("dokka/html"))
    }

public abstract class CodenarcExtension
    @Inject
    constructor(
        project: Project,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            project.objects.property<Boolean>().convention(
                gradleProperty(
                    project,
                    ConfigurationConstants.CODENARC_ENABLED,
                    String::toBoolean,
                ).orElse(true),
            )
        public val toolVersion: Property<String> =
            project.objects.property<String>().convention(
                gradleProperty(
                    project,
                    ConfigurationConstants.CODENARC_VERSION,
                ).orElse(libs.findVersion("codenarc").get().requiredVersion),
            )
        public val failOnViolation: Property<Boolean> = project.objects.property<Boolean>().convention(true)
        public val configFile: RegularFileProperty = project.objects.fileProperty()
    }
