package extensions

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

@Suppress("TooManyFunctions")
public abstract class QualityExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val checkstyle: CheckstyleExtension = objects.newInstance()
        public val spotbugs: SpotbugsExtension = objects.newInstance()
        public val pmd: PmdExtension = objects.newInstance()
        public val jacoco: JacocoExtension = objects.newInstance()
        public val detekt: DetektExtension = objects.newInstance()
        public val spotless: SpotlessExtension = objects.newInstance()
        public val owaspDependencyCheck: OwaspDependencyCheckExtension = objects.newInstance()
        public val versions: GradleVersionExtension = objects.newInstance()
        public val pitest: PitestExtension = objects.newInstance()
        public val errorProne: ErrorProneExtension = objects.newInstance()
        public val taskScanner: TaskScannerExtension = objects.newInstance()
        public val kover: KoverExtension = objects.newInstance()
        public val eslint: EslintExtension = objects.newInstance()
        public val dokka: DokkaExtension = objects.newInstance()

        public fun checkstyle(action: CheckstyleExtension.() -> Unit): Unit = action(checkstyle)

        public fun spotbugs(action: SpotbugsExtension.() -> Unit): Unit = action(spotbugs)

        public fun pmd(action: PmdExtension.() -> Unit): Unit = action(pmd)

        public fun jacoco(action: JacocoExtension.() -> Unit): Unit = action(jacoco)

        public fun detekt(action: DetektExtension.() -> Unit): Unit = action(detekt)

        public fun spotless(action: SpotlessExtension.() -> Unit): Unit = action(spotless)

        public fun owaspDependencyCheck(action: OwaspDependencyCheckExtension.() -> Unit): Unit =
            action(owaspDependencyCheck)

        public fun pitest(action: PitestExtension.() -> Unit): Unit = action(pitest)

        public fun errorProne(action: ErrorProneExtension.() -> Unit): Unit = action(errorProne)

        public fun versions(action: GradleVersionExtension.() -> Unit): Unit = action(versions)

        public fun taskScanner(action: TaskScannerExtension.() -> Unit): Unit = action(taskScanner)

        public fun kover(action: KoverExtension.() -> Unit): Unit = action(kover)

        public fun eslint(action: EslintExtension.() -> Unit): Unit = action(eslint)

        public fun dokka(action: DokkaExtension.() -> Unit): Unit = action(dokka)

        public companion object {
            public const val DEFAULT_COVERAGE_THRESHOLD: Double = 0.80
            public const val DEFAULT_OWASP_THRESHOLD: Float = 7.0f
            public const val DEFAULT_MUTATION_THRESHOLD: Int = 85
            public const val DEFAULT_MAX_ALLOWED_TASKS: Int = 10
            public const val DEFAULT_KOVER_THRESHOLD: Int = 80
        }
    }

public abstract class CheckstyleExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val toolVersion: Property<String> = objects.property<String>()
        public val configFile: Property<String> = objects.property<String>().convention("")
        public val failOnViolation: Property<Boolean> = objects.property<Boolean>().convention(true)
    }

public abstract class SpotbugsExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val toolVersion: Property<String> = objects.property<String>()
        public val effortLevel: Property<String> = objects.property<String>().convention("DEFAULT")
        public val reportLevel: Property<String> = objects.property<String>().convention("DEFAULT")
        public val excludeFilterFile: RegularFileProperty = objects.fileProperty()
    }

public abstract class PmdExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val toolVersion: Property<String> = objects.property<String>()
        public val ruleSets: RegularFileProperty = objects.fileProperty()
        public val enableCPD: Property<Boolean> = objects.property<Boolean>().convention(false)
    }

public abstract class JacocoExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val toolVersion: Property<String> = objects.property<String>()
        public val coverageThreshold: Property<Double> =
            objects.property<Double>().convention(QualityExtension.DEFAULT_COVERAGE_THRESHOLD)
    }

public abstract class DetektExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val toolVersion: Property<String> = objects.property<String>()
        public val autoCorrect: Property<Boolean> = objects.property<Boolean>().convention(false)
        public val failOnViolation: Property<Boolean> = objects.property<Boolean>().convention(false)
    }

public abstract class SpotlessExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val ktlintVersion: Property<String> = objects.property<String>().convention("")
        public val googleJavaFormatVersion: Property<String> = objects.property<String>().convention("")
    }

public abstract class OwaspDependencyCheckExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val failOnCvss: Property<Float> =
            objects.property<Float>().convention(QualityExtension.DEFAULT_OWASP_THRESHOLD)
        public val format: Property<String> = objects.property<String>().convention("ALL")
    }

public abstract class PitestExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(false)
        public val toolVersion: Property<String> = objects.property<String>().convention("")
        public val threads: Property<Int> = objects.property<Int>().convention(0)
        public val outputFormats: ListProperty<String> =
            objects.listProperty<String>().convention(
                listOf("XML", "HTML"),
            )
        public val mutationThreshold: Property<Int> =
            objects.property<Int>().convention(QualityExtension.DEFAULT_MUTATION_THRESHOLD)
    }

public abstract class ErrorProneExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val toolVersion: Property<String> = objects.property<String>().convention("")
    }

public abstract class GradleVersionExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val checkForStableVersionsOnly: Property<Boolean> = objects.property<Boolean>().convention(true)
    }

public abstract class TaskScannerExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val patterns: ListProperty<String> =
            objects.listProperty<String>().convention(
                listOf("TODO", "FIXME", "HACK"),
            )
        public val maxAllowedTasks: Property<Int> =
            objects.property<Int>().convention(QualityExtension.DEFAULT_MAX_ALLOWED_TASKS)
        public val failOnExceed: Property<Boolean> = objects.property<Boolean>().convention(false)
    }

public abstract class KoverExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(false)
        public val toolVersion: Property<String> = objects.property<String>().convention("")
        public val coverageThreshold: Property<Int> =
            objects.property<Int>().convention(QualityExtension.DEFAULT_KOVER_THRESHOLD)
    }

public abstract class EslintExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(false)
        public val configFile: RegularFileProperty = objects.fileProperty()
        public val autofix: Property<Boolean> = objects.property<Boolean>().convention(false)
    }

public abstract class DokkaExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)
    }
