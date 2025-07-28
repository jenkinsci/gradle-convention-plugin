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
@file:Suppress("ktlint:standard:no-wildcard-imports")

package extensions

import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import constants.ConfigurationConstants
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.provider.SetProperty
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty
import utils.gradleProperty
import utils.versionFromCatalogOrFail
import javax.inject.Inject

@Suppress("TooManyFunctions")
public open class QualityExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val checkstyle: CheckstyleExtension = objects.newInstance(libs)
        public val codenarc: CodenarcExtension = objects.newInstance(libs)
        public val spotbugs: SpotbugsExtension = objects.newInstance(libs)
        public val pmd: PmdExtension = objects.newInstance(libs)
        public val jacoco: JacocoExtension = objects.newInstance(libs)
        public val detekt: DetektExtension = objects.newInstance(libs)
        public val spotless: SpotlessExtension = objects.newInstance(libs)
        public val owaspDependencyCheck: OwaspDependencyCheckExtension = objects.newInstance()
        public val versions: GradleVersionExtension = objects.newInstance()
        public val pitest: PitestExtension = objects.newInstance(libs)
        public val kover: KoverExtension = objects.newInstance()
        public val eslint: EslintExtension = objects.newInstance()
        public val dokka: DokkaExtension = objects.newInstance()
        public val cpd: CpdExtension = objects.newInstance(libs)

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

        public fun cpd(action: CpdExtension.() -> Unit): Unit = action(cpd)

        public companion object {
            public const val DEFAULT_CODE_COVERAGE_THRESHOLD: Double = 0.8
            public const val DEFAULT_OWASP_THRESHOLD: Float = 7.0f
            public const val DEFAULT_MUTATION_THRESHOLD: Int = 85
            public const val DEFAULT_KOVER_THRESHOLD: Int = 80
            public const val DEFAULT_THREADS: Int = 4
            public const val DEFAULT_TOKEN_COUNT: Int = 50
        }
    }

public open class CheckstyleExtension
    @Inject
    constructor(
        libs: VersionCatalog,
        objects: ObjectFactory,
        providers: ProviderFactory,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.CHECKSTYLE_ENABLED,
                    String::toBoolean,
                ).orElse(true),
            )
        public val toolVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.CHECKSTYLE_VERSION,
                ).orElse(versionFromCatalogOrFail(libs, "checkstyle")),
            )
        public val failOnViolation: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val source: Property<String> = objects.property<String>().convention("src")
        public val include: ListProperty<String> = objects.listProperty<String>().convention(listOf("**/*.java"))
        public val exclude: ListProperty<String> =
            objects.listProperty<String>().convention(
                listOf(
                    "**/generated/**",
                    "**/target/**",
                    "**/build/**",
                    "**/Messages.class",
                    "**/*Descriptor.java",
                    "**/jelly/**",
                    "**/tags/**",
                ),
            )
    }

public open class SpotbugsExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.SPOTBUGS_ENABLED,
                    String::toBoolean,
                ).orElse(true),
            )
        public val toolVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.SPOTBUGS_VERSION,
                ).orElse(versionFromCatalogOrFail(libs, "spotbugsTool")),
            )
        public val effortLevel: Property<Effort> = objects.property<Effort>().convention(Effort.MAX)
        public val reportLevel: Property<Confidence> = objects.property<Confidence>().convention(Confidence.LOW)
        public val failOnError: Property<Boolean> = objects.property<Boolean>().convention(true)
    }

public open class PmdExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.PMD_ENABLED,
                    String::toBoolean,
                ).orElse(true),
            )
        public val toolVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(providers, ConfigurationConstants.PMD_VERSION).orElse(
                    versionFromCatalogOrFail(libs, "pmd"),
                ),
            )
        public val consoleOutput: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val failOnViolation: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val source: Property<String> = objects.property<String>().convention("src")
        public val include: ListProperty<String> = objects.listProperty<String>().convention(listOf("**/*.java"))
        public val exclude: ListProperty<String> =
            objects.listProperty<String>().convention(
                listOf(
                    "**/generated/**",
                    "**/target/**",
                    "**/build/**",
                    "**/Messages.class",
                    "**/*Descriptor.java",
                    "**/jelly/**",
                    "**/tags/**",
                ),
            )
    }

public open class JacocoExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.JACOCO_ENABLED,
                    String::toBoolean,
                ).orElse(true),
            )
        public val toolVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(providers, ConfigurationConstants.JACOCO_VERSION).orElse(
                    versionFromCatalogOrFail(libs, "jacoco"),
                ),
            )
        public val minimumCodeCoverage: Property<Double> =
            objects.property<Double>().convention(
                QualityExtension.DEFAULT_CODE_COVERAGE_THRESHOLD,
            )
        public val excludes: ListProperty<String> =
            objects.listProperty<String>().convention(
                listOf(
                    "**/generated/**",
                    "**/target/**",
                    "**/build/**",
                    "**/Messages.class",
                    "**/*Descriptor.class",
                    "**/jelly/**",
                    "**/tags/**",
                ),
            )
    }

public open class DetektExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.DETEKT_ENABLED,
                    String::toBoolean,
                ).orElse(true),
            )
        public val toolVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(providers, ConfigurationConstants.DETEKT_VERSION).orElse(
                    versionFromCatalogOrFail(libs, "detekt"),
                ),
            )
        public val autoCorrect: Property<Boolean> = objects.property<Boolean>().convention(false)
        public val failOnViolation: Property<Boolean> = objects.property<Boolean>().convention(false)
        public val source: ListProperty<String> =
            objects.listProperty<String>().convention(
                listOf("src/main/java", "src/main/kotlin"),
            )
    }

public open class SpotlessExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.SPOTLESS_ENABLED,
                    String::toBoolean,
                ).orElse(false),
            )
    }

public open class OwaspDependencyCheckExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        layout: ProjectLayout,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.OWASP_ENABLED,
                    String::toBoolean,
                ).orElse(false),
            )
        public val failOnCvss: Property<Float> =
            objects.property<Float>().convention(QualityExtension.DEFAULT_OWASP_THRESHOLD)
        public val formats: ListProperty<String> =
            objects.listProperty<String>().convention(
                setOf("XML", "HTML", "SARIF"),
            )
        public val dataDirectory: DirectoryProperty =
            objects.directoryProperty().convention(
                layout.projectDirectory.dir(".gradle/dependency-check-data"),
            )
        public val outputDirectory: DirectoryProperty =
            objects.directoryProperty().convention(
                layout.buildDirectory.dir("reports/dependency-check"),
            )
        public val suppressionFiles: ListProperty<RegularFile> =
            objects
                .listProperty<RegularFile>()
                .convention(
                    emptyList(),
                )
        public val scanConfigurations: ListProperty<String> =
            objects.listProperty<String>().convention(
                listOf("runtimeClasspath", "compileClasspath"),
            )
    }

public open class PitestExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.PITEST_ENABLED,
                    String::toBoolean,
                ).orElse(false),
            )
        public val pitVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(providers, ConfigurationConstants.PITEST_VERSION).orElse(
                    versionFromCatalogOrFail(libs, "pit"),
                ),
            )
        public val threads: Property<Int> = objects.property<Int>().convention(QualityExtension.DEFAULT_THREADS)
        public val targetClasses: ListProperty<String> = objects.listProperty<String>().convention(listOf("*"))
        public val excludedClasses: ListProperty<String> =
            objects.listProperty<String>().convention(listOf("*Test*"))
        public val mutationThreshold: Property<Int> =
            objects.property<Int>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.PITEST_MUTATION_THRESHOLD,
                    String::toInt,
                ).orElse(QualityExtension.DEFAULT_MUTATION_THRESHOLD),
            )
        public val outputFormats: ListProperty<String> =
            objects.listProperty<String>().convention(
                listOf("XML", "HTML"),
            )
        public val mutators: SetProperty<String> = objects.setProperty<String>().convention(setOf("DEFAULT"))
    }

public open class GradleVersionExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
    }

public open class KoverExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.KOVER_ENABLED,
                    String::toBoolean,
                ).orElse(true),
            )
        public val coverageThreshold: Property<Int> =
            objects.property<Int>().convention(QualityExtension.DEFAULT_KOVER_THRESHOLD)
    }

public open class EslintExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.ESLINT_ENABLED,
                    String::toBoolean,
                ).orElse(false),
            )
        public val autofix: Property<Boolean> = objects.property<Boolean>().convention(false)
        public val configFile: RegularFileProperty = objects.fileProperty()
    }

public open class DokkaExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        layout: ProjectLayout,
        providers: ProviderFactory,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.DOKKA_ENABLED,
                    String::toBoolean,
                ).orElse(true),
            )
        public val outputDirectory: DirectoryProperty =
            objects.directoryProperty().convention(layout.buildDirectory.dir("dokka/html"))
    }

public open class CodenarcExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.CODENARC_ENABLED,
                    String::toBoolean,
                ).orElse(false),
            )
        public val toolVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.CODENARC_VERSION,
                ).orElse(
                    versionFromCatalogOrFail(libs, "codenarc"),
                ),
            )

        public val failOnViolation: Property<Boolean> = objects.property<Boolean>().convention(true)

        public val configFile: RegularFileProperty = objects.fileProperty()
    }

public open class CpdExtension
    @Inject
    constructor(
        libs: VersionCatalog,
        objects: ObjectFactory,
        providers: ProviderFactory,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.CPD_ENABLED,
                    String::toBoolean,
                ).orElse(true),
            )
        public val failOnViolation: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val source: Property<String> = objects.property<String>().convention("src")
        public val minimumTokenCount: Property<Int> =
            objects.property<Int>().convention(QualityExtension.DEFAULT_TOKEN_COUNT)
    }
