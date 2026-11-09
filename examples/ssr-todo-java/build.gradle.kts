import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.html.lowering) // optional, lower static parts at compile time for faster rendering
    alias(libs.plugins.spring.boot)
}

description = "Server-rendered TODO application with Java Spring code and Kotlin pages"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
    }
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.kotlin.bom))
    implementation(project(":spring-compose-html-webmvc"))
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")

    runtimeOnly("com.h2database:h2")

    developmentOnly(platform(libs.spring.boot.dependencies))
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
