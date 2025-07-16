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
package internal

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.tasks.compile.GroovyCompile
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

private const val JAVA_VERSION = 17

public class GroovyConventionManager(
    private val project: Project,
    private val libs: VersionCatalog,
) {
    public fun configure() {
        project.plugins.withId("groovy") {
            project.tasks.withType<GroovyCompile>().configureEach {
                it.groovyOptions.encoding = "UTF-8"
                it.groovyOptions.optimizationOptions?.put("indy", true)
                it.options.release.set(JAVA_VERSION)
                it.options.compilerArgs.addAll(listOf("-parameters"))
            }

            project.dependencies {
                add("compileOnly", platform(libs.findLibrary("groovy-bom").get()))
                add("compileOnly", libs.findLibrary("groovy").get())
            }
        }
    }
}
