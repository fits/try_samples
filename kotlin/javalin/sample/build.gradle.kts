import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    application
}

repositories {
    mavenCentral()
}


tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "15"
}

application {
    mainClass.set("MainKt")
}

dependencies {
    implementation("io.javalin:javalin:3.12.0")

    runtimeOnly("org.slf4j:slf4j-simple:1.7.30")
    runtimeOnly("com.fasterxml.jackson.core:jackson-databind:2.10.3")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.3")
}