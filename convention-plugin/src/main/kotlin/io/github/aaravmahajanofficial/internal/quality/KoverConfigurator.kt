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

import io.github.aaravmahajanofficial.extensions.quality.QualityExtension
import io.github.aaravmahajanofficial.utils.hasKotlinSources
import kotlinx.kover.gradle.plugin.KoverGradlePlugin
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureKoverExtension(quality: QualityExtension) {
    if (!quality.kover.enabled.get() || !hasKotlinSources()) return

    pluginManager.apply(KoverGradlePlugin::class.java)

    configure<KoverProjectExtension> {
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
    tasks.named("check").configure { t ->
        t.dependsOn("koverVerify")
    }
}
