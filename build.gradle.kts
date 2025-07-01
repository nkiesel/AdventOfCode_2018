plugins {
    kotlin("jvm") version "2.2.0"
    alias(libs.plugins.versions.update)
}

version = "2018"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.kotest.assertions.core)
    implementation(libs.junit.jupiter)
    implementation(libs.junit.jupiter.api)
    implementation(libs.junit.jupiter.engine)
    implementation(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
    minHeapSize = "1g"
    maxHeapSize = "30g"
    testLogging.showStandardStreams = true
    filter {
        setIncludePatterns("*Test", "Day*")
    }
}

kotlin {
    jvmToolchain(21)
    @Suppress("UnsafeCompilerArguments")
    compilerOptions {
        suppressWarnings = true
        freeCompilerArgs.set(
            listOf(
                "-Xcontext-sensitive-resolution",
                "-Xcontext-parameters",
            )
        )
    }
}
