import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.example.services"
version = "1.0.0"

application {
    mainClass.set("com.example.services.order.OrderServiceKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core:2.3.6")
    implementation("io.ktor:ktor-server-netty:2.3.6")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.6")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.6")
    implementation("io.ktor:ktor-server-cors:2.3.6")
    implementation("io.ktor:ktor-server-status-pages:2.3.6")
    implementation("io.ktor:ktor-server-call-logging:2.3.6")
    implementation("io.ktor:ktor-client-core:2.3.6")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.6")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.6")
    implementation("io.ktor:ktor-client-okhttp:2.3.6")
    
    // Firebase Admin SDK
    implementation("com.google.firebase:firebase-admin:9.2.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")
    
    // Shared models
    implementation(project(":shared-models"))
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.11")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.shadowJar {
    archiveBaseName.set("order-service")
    archiveClassifier.set("")
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    mergeServiceFiles()
}

