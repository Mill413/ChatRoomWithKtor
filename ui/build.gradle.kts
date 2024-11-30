plugins {
    kotlin("jvm")
    id("com.gradleup.shadow") version "9.0.0-beta2"
}

group = "top.mill"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation(project(":backend"))

    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveClassifier.set("shadow") // 不添加 "-all" 后缀
    manifest {
        attributes["Main-Class"] = "top.mill.MainKt" // 指定入口类
    }
}

kotlin {
    jvmToolchain(21)
}