import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
}

internal val Project.libs: VersionCatalog get() = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    explicitApi()

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
        allWarningsAsErrors.set(true)

        progressiveMode.set(true)

        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-Xjvm-default=all",
            "-opt-in=kotlin.RequiresOptIn",
            "-Xtype-enhancement-improvements-strict-mode"
        )
    }
}

dependencies {
    implementation(platform(libs.findLibrary("kotlin-bom").get()))
    implementation(libs.findLibrary("kotlin-stdlib").get())
    implementation(libs.findLibrary("kotlin-reflect").get())
}