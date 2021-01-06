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
    implementation("com.graphql-java:graphql-java:16.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.4.2")

    runtimeOnly("org.slf4j:slf4j-nop:1.7.30")
}
