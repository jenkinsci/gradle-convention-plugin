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
package io.github.aaravmahajanofficial.internal

import io.github.aaravmahajanofficial.extensions.PluginExtension
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register

public class TestJarConfig(
    private val project: Project,
    private val ext: PluginExtension,
) {
    public fun configure() {
        if (!ext.publishTestJar.get()) return

        val sourceSets = project.extensions.getByType<SourceSetContainer>()
        val testSourceSet = sourceSets.getByName("test")

        val testJar = project.tasks.register<Jar>("testJar")
        testJar.configure { t ->
            t.group = "Build"
            t.description = "Assembles a jar archive containing the test classes"
            t.archiveClassifier.set("tests")
            t.from(testSourceSet.output)
            t.duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            t.dependsOn(project.tasks.named(testSourceSet.classesTaskName))
        }

        project.plugins.withId("maven-publish") {
            project.extensions.configure(PublishingExtension::class.java) { pubExt ->
                pubExt.publications.named<MavenPublication>("mavenJpi").configure {
                    it.artifact(testJar.get())
                }
            }
        }
    }
}
