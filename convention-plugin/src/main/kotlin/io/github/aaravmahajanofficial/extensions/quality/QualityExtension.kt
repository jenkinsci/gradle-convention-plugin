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
package io.github.aaravmahajanofficial.extensions.quality

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

@Suppress("TooManyFunctions")
public open class QualityExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val checkstyle: CheckstyleExtension = objects.newInstance()
        public val codenarc: CodenarcExtension = objects.newInstance()
        public val spotbugs: SpotbugsExtension = objects.newInstance()
        public val pmd: PmdExtension = objects.newInstance()
        public val jacoco: JacocoExtension = objects.newInstance()
        public val detekt: DetektExtension = objects.newInstance()
        public val spotless: SpotlessExtension = objects.newInstance()
        public val owaspDependencyCheck: OwaspDepCheckExtension = objects.newInstance()
        public val pitest: PitestExtension = objects.newInstance()
        public val kover: KoverExtension = objects.newInstance()
        public val eslint: EslintExtension = objects.newInstance()
        public val dokka: DokkaExtension = objects.newInstance()
        public val cpd: CpdExtension = objects.newInstance()

        public fun checkstyle(action: Action<CheckstyleExtension>): Unit = action.execute(checkstyle)

        public fun codenarc(action: Action<CodenarcExtension>): Unit = action.execute(codenarc)

        public fun spotbugs(action: Action<SpotbugsExtension>): Unit = action.execute(spotbugs)

        public fun pmd(action: Action<PmdExtension>): Unit = action.execute(pmd)

        public fun jacoco(action: Action<JacocoExtension>): Unit = action.execute(jacoco)

        public fun detekt(action: Action<DetektExtension>): Unit = action.execute(detekt)

        public fun spotless(action: Action<SpotlessExtension>): Unit = action.execute(spotless)

        public fun owaspDepCheck(action: Action<OwaspDepCheckExtension>): Unit = action.execute(owaspDependencyCheck)

        public fun pitest(action: Action<PitestExtension>): Unit = action.execute(pitest)

        public fun kover(action: Action<KoverExtension>): Unit = action.execute(kover)

        public fun eslint(action: Action<EslintExtension>): Unit = action.execute(eslint)

        public fun dokka(action: Action<DokkaExtension>): Unit = action.execute(dokka)

        public fun cpd(action: Action<CpdExtension>): Unit = action.execute(cpd)

        public companion object {
            public const val DEFAULT_CODE_COVERAGE_THRESHOLD: Double = 0.8
            public const val DEFAULT_OWASP_THRESHOLD: Float = 7.0f
            public const val DEFAULT_MUTATION_THRESHOLD: Int = 85
            public const val DEFAULT_KOVER_THRESHOLD: Int = 80
            public const val DEFAULT_THREADS: Int = 4
            public const val DEFAULT_TOKEN_COUNT: Int = 50
        }
    }

internal val excludeList =
    listOf(
        "**/generated/**",
        "**/build/**",
        "**/target/**",
        "**/Messages.java",
        "**/*Descriptor.java",
        "**/jelly/**",
        "**/tags/**",
    )
