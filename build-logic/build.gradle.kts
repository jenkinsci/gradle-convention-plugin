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

    compilerOptions {
        apiVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1
        languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17

        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}