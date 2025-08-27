plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotest)
    alias(libs.plugins.versions.update)
}

version = "2018"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest)
}

tasks.test {
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
                "-Xnested-type-aliases",
            )
        )
    }
}
