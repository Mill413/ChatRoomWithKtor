val ktorVersion: String = "2.2.1"
val kotlinVersion: String = "1.7.20"
val logbackVersion: String = "1.2.9"

plugins {
    application
    kotlin("jvm") version "1.7.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "top.harumill"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("org.xerial", "sqlite-jdbc", "3.8.11.2")

    implementation("org.jline", "jline", "3.20.0")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "16"
    freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
}
