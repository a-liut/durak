plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.kotlin.jvm)
    // Apply Ktlint plugin (https://github.com/JLLeitschuh/ktlint-gradle)
    alias(libs.plugins.ktlint)
    // Apply shadow plugin (https://github.com/GradleUp/shadow)
    alias(libs.plugins.shadow)
    // Apply changelog plugin (https://github.com/JetBrains/gradle-changelog-plugin)
    alias(libs.plugins.changelog)
    // Apply detekt plugin
    alias(libs.plugins.detekt)

    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

group = "it.aliut"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    // Use coroutines.
    implementation(libs.kotlinx.coroutines.core)

    // Logging
    implementation(libs.kotlin.logging)
    implementation(libs.logback)

    // Clikt for cli interface
    implementation(libs.clikt)

    // Use the Kotlin JUnit 5 integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // Use the JUnit 5 integration.
    testImplementation(libs.junit.jupiter.engine)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    // Define the main class for the application.
    mainClass = "it.aliut.durak.MainKt"
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`

    // Reduce the SLF4J internal logging verbosity.
    systemProperty("slf4j.internal.verbosity", "WARN")
}
