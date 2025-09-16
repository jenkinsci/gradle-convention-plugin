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
package io.github.aaravmahajanofficial.internal.quality

import com.github.spotbugs.snom.SpotBugsExtension
import com.github.spotbugs.snom.SpotBugsPlugin
import com.github.spotbugs.snom.SpotBugsTask
import io.github.aaravmahajanofficial.extensions.quality.QualityExtension
import io.github.aaravmahajanofficial.utils.hasJavaSources
import io.github.aaravmahajanofficial.utils.resolveConfigFile
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

internal fun Project.configureSpotBugs(quality: QualityExtension) {
    if (!quality.spotbugs.enabled.get() || !hasJavaSources()) return

    pluginManager.apply(SpotBugsPlugin::class.java)

    configure<SpotBugsExtension> {
        effort.set(quality.spotbugs.effortLevel)
        reportLevel.set(quality.spotbugs.reportLevel)
        ignoreFailures.set(quality.spotbugs.failOnError.map { !it })
        excludeFilter.set(resolveConfigFile("spotbugs", "excludesFilter.xml"))
        omitVisitors.addAll(
            listOf(
                "ConstructorThrow",
                "FindReturnRef",
                "MultipleInstantiationsOfSingletons",
                "SharedVariableAtomicityDetector",
                "ThrowingExceptions",
            ),
        )
        omitVisitors.addAll(quality.spotbugs.omitVisitors)
    }

    tasks.withType<SpotBugsTask>().configureEach {
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

    tasks.named("check").configure { t ->
        t.dependsOn("spotbugsMain")
    }
}
