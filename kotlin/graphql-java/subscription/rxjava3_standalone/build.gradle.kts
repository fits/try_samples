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
    implementation("io.reactivex.rxjava3:rxjava:3.0.9")

    runtimeOnly("org.slf4j:slf4j-nop:1.7.30")
}
