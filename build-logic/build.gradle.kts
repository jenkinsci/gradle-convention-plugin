import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
}

group = "io.jenkins.gradle.conventions"
version = rootProject.version

val javaToolchainVersion: Provider<Int> =
    providers.gradleProperty("java.toolchain.version").map(String::toInt).orElse(17)

java {
    toolchain {
        languageVersion = javaToolchainVersion.map(JavaLanguageVersion::of)
    }
}

kotlin {
    jvmToolchain {
        languageVersion = javaToolchainVersion.map(JavaLanguageVersion::of)
    }

    explicitApi()

    compilerOptions {
        apiVersion =
            org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1
        languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1
        jvmTarget = JvmTarget.JVM_17

        allWarningsAsErrors.set(true)
        progressiveMode.set(false)

        optIn.add("kotlin.RequiresOptIn")
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-Xjvm-default=all",
        )
    }
}

sourceSets {
    main {
        java.setSrcDirs(emptyList<String>())
        kotlin.setSrcDirs(listOf("src/main/kotlin"))
    }
    test {
        java.setSrcDirs(emptyList<String>())
        kotlin.setSrcDirs(listOf("src/test/kotlin"))
    }
}

tasks.withType<JavaCompile>().configureEach {
    source = project.files().asFileTree
}

gradlePlugin {
    plugins {
        create("javaConventions") {
            id = "conventions.java"
            displayName = "Java Conventions"
            implementationClass = "conventions.JavaConventionsPlugin"
        }
    }
    plugins {
        create("kotlinConventions") {
            id = "conventions.kotlin"
            displayName = "Kotlin Conventions"
            implementationClass = "conventions.KotlinConventionsPlugin"
        }
    }
    plugins {
        create("qualityConventions") {
            id = "conventions.quality"
            displayName = "Quality Conventions"
            implementationClass = "conventions.QualityConventionsPlugin"
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.spotless.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.ktlint.gradle.plugin)
}
