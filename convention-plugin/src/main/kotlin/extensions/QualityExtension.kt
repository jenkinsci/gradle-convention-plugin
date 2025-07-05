package extensions

import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
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
        private val objects: ObjectFactory = project.objects

        public val checkstyle: CheckstyleExtension =
            project.extensions.create("checkstyle", CheckstyleExtension::class.java, objects, project, libs)
        public val spotbugs: SpotbugsExtension =
            project.extensions.create("spotbugs", SpotbugsExtension::class.java, objects, libs)
        public val pmd: PmdExtension = project.extensions.create("pmd", PmdExtension::class.java, objects, libs)
        public val jacoco: JacocoExtension =
            project.extensions.create(
                "jacoco",
                JacocoExtension::class.java,
                objects,
                libs,
            )
        public val detekt: DetektExtension =
            project.extensions.create("detekt", DetektExtension::class.java, objects, project, libs)
        public val spotless: SpotlessExtension =
            project.extensions.create("spotless", SpotlessExtension::class.java, objects)
        public val owaspDependencyCheck: OwaspDependencyCheckExtension =
            project.extensions.create(
                "owaspDependencyCheck",
                OwaspDependencyCheckExtension::class.java,
                objects,
                project,
            )
        public val versions: GradleVersionExtension =
            project.extensions.create("versions", GradleVersionExtension::class.java, objects)
        public val pitest: PitestExtension = project.extensions.create("pitest", PitestExtension::class.java, objects)
        public val kover: KoverExtension = project.extensions.create("Kover", KoverExtension::class.java, objects)
        public val eslint: EslintExtension =
            project.extensions.create("eslint", EslintExtension::class.java, objects, project)
        public val dokka: DokkaExtension = project.extensions.create("dokka", DokkaExtension::class.java, objects)

        public fun checkstyle(action: CheckstyleExtension.() -> Unit): Unit = action(checkstyle)

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
            public const val DEFAULT_CODE_COVERAGE_THRESHOLD: Double = 80.0
            public const val DEFAULT_OWASP_THRESHOLD: Float = 7.0f
            public const val DEFAULT_MUTATION_THRESHOLD: Int = 85
            public const val DEFAULT_KOVER_THRESHOLD: Int = 80
            public const val DEFAULT_THREADS: Int = 4
        }
    }

public abstract class CheckstyleExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        project: Project,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val toolVersion: Property<String> =
            objects.property<String>().convention(libs.findVersion("checkstyle").get().requiredVersion)
        public val failOnViolation: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val configFile: RegularFileProperty = objects.fileProperty()
    }

public abstract class SpotbugsExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val toolVersion: Property<String> =
            objects.property<String>().convention(libs.findVersion("spotbugs").get().requiredVersion)
        public val effortLevel: Property<Effort> = objects.property<Effort>().convention(Effort.MAX)
        public val reportLevel: Property<Confidence> = objects.property<Confidence>().convention(Confidence.LOW)
        public val excludeFilterFile: RegularFileProperty = objects.fileProperty()
    }

public abstract class PmdExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val toolVersion: Property<String> =
            objects.property<String>().convention(libs.findVersion("pmd").get().requiredVersion)
        public val enableCPD: Property<Boolean> = objects.property<Boolean>().convention(false)
        public val consoleOutput: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val failOnViolation: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val ruleSetFiles: RegularFileProperty = objects.fileProperty()
    }

public abstract class JacocoExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val toolVersion: Property<String> =
            objects.property<String>().convention(libs.findVersion("jacoco").get().requiredVersion)
        public val minimumCodeCoverage: Property<Double> =
            objects.property<Double>().convention(
                QualityExtension.DEFAULT_CODE_COVERAGE_THRESHOLD,
            )
        public val excludes: ListProperty<String> = objects.listProperty<String>().convention(emptyList())
    }

public abstract class DetektExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        project: Project,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val toolVersion: Property<String> =
            objects.property<String>().convention(libs.findVersion("detekt").get().requiredVersion)
        public val autoCorrect: Property<Boolean> = objects.property<Boolean>().convention(false)
        public val failOnViolation: Property<Boolean> = objects.property<Boolean>().convention(false)
        public val source: ListProperty<String> =
            objects.listProperty<String>().convention(
                listOf("src/main/java", "src/main/kotlin"),
            )
        public val configFile: RegularFileProperty = objects.fileProperty()
        public val baseline: RegularFileProperty = objects.fileProperty()
    }

public abstract class SpotlessExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val ktlintVersion: Property<String> =
            objects.property<String>().convention(libs.findVersion("ktlint").get().requiredVersion)
        public val googleJavaFormatVersion: Property<String> =
            objects.property<String>().convention(libs.findVersion("googleJavaFormat").get().requiredVersion)
    }

public abstract class OwaspDependencyCheckExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        project: Project,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val failOnCvss: Property<Float> =
            objects.property<Float>().convention(QualityExtension.DEFAULT_OWASP_THRESHOLD)
        public val formats: ListProperty<String> =
            objects.listProperty<String>().convention(
                setOf("XML", "HTML", "SARIF"),
            )
        public val dataDirectory: DirectoryProperty =
            objects.directoryProperty().convention(
                project.layout.projectDirectory.dir(".gradle/dependency-check-data"),
            )
        public val outputDirectory: DirectoryProperty =
            objects.directoryProperty().convention(
                project.layout.buildDirectory.dir("reports/dependency-check"),
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

public abstract class PitestExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(false)
        public val pitVersion: Property<String> =
            objects.property<String>().convention(libs.findVersion("pit").get().requiredVersion)
        public val threads: Property<Int> = objects.property<Int>().convention(QualityExtension.DEFAULT_THREADS)
        public val targetClasses: ListProperty<String> = objects.listProperty<String>().convention(listOf("*"))
        public val excludedClasses: ListProperty<String> = objects.listProperty<String>().convention(listOf("*Test*"))
        public val mutationThreshold: Property<Int> =
            objects.property<Int>().convention(QualityExtension.DEFAULT_MUTATION_THRESHOLD)
        public val outputFormats: ListProperty<String> =
            objects.listProperty<String>().convention(
                listOf("XML", "HTML"),
            )
        public val mutators: SetProperty<String> = objects.setProperty<String>().convention(setOf("DEFAULT"))
    }

public abstract class GradleVersionExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
    }

public abstract class KoverExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(false)
        public val coverageThreshold: Property<Int> =
            objects.property<Int>().convention(QualityExtension.DEFAULT_KOVER_THRESHOLD)
    }

public abstract class EslintExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        project: Project,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(false)
        public val autofix: Property<Boolean> = objects.property<Boolean>().convention(false)
        public val configFile: RegularFileProperty = objects.fileProperty()
    }

public abstract class DokkaExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val formats: SetProperty<String> = objects.setProperty<String>().convention(setOf("gfm"))
    }
