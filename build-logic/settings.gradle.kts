rootProject.name = "build-logic"

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }

    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
