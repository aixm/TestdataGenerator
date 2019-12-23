import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.61"
    application
}

group = "com.solitec.aixm"
version = "1.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.apache.ws.xmlschema:xmlschema-core:2.2.4")
    implementation("com.github.ajalt:clikt:2.1.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "com.solitec.aixm.datagen.MainKt"
}