import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.compose)
}

description = "Spring MVC integration for Compose HTML"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    explicitApi()
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    api(platform(libs.spring.framework.bom))
    api(libs.compose.runtime)
    api(libs.compose.html.core)
    api(libs.kotlinx.browser.common.subset)

    compileOnly("org.springframework:spring-webmvc")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")

    testImplementation(platform(libs.spring.boot.dependencies))
    testImplementation("org.springframework:spring-webmvc")
    testImplementation("org.springframework:spring-test")
    testImplementation(platform(libs.kotlin.bom))
    testImplementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("jakarta.servlet:jakarta.servlet-api")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
