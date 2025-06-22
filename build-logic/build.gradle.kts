import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.gradlePlugin)

    implementation(libs.spotless.gradlePlugin)
}

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
        progressiveMode.set(true)

        optIn.add("kotlin.RequiresOptIn")
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-Xjvm-default=all",
        )
    }
}
