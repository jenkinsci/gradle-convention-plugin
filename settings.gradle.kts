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
pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.1.6"
}

gitHooks {
    commitMsg { conventionalCommits() }
    createHooks()
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenCentral()
        gradlePluginPortal()

        maven {
            name = "jenkinsPublic"
            url = uri("https://repo.jenkins-ci.org/public/")
        }
    }

    versionCatalogs {
        create(
            "baseLibs",
            Action {
                from(files("version-catalog/libs.versions.toml"))
            },
        )
    }
}

rootProject.name = "jenkins-gradle-convention-plugin"

include("convention-plugin", "version-catalog")
