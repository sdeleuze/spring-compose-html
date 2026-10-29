import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
}

description = "Spring Boot server rendering the hydration islands page"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
    }
}

// Browser bundle built by the frontend module, served from the "static" resources directory
val browserDistribution = configurations.dependencyScope("browserDistribution")
val browserDistributionFiles = configurations.resolvable("browserDistributionFiles") {
    extendsFrom(browserDistribution.get())
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.kotlin.bom))
    implementation(project(":spring-compose-html"))
    implementation(project(":examples:hydration-islands:shared"))
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    browserDistribution.name(project(path = ":examples:hydration-islands:frontend", configuration = "browserDistribution"))

    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.processResources {
    from(browserDistributionFiles) {
        into("static")
    }
}

tasks.bootJar {
    // org.jetbrains.compose.runtime:runtime-desktop is an empty stub with the same file name as
    // androidx.compose.runtime:runtime-desktop: keep only the latter in the executable jar
    val stub = "org.jetbrains.compose.runtime${File.separator}runtime-desktop${File.separator}"
    setClasspath(classpath!!.filter { !it.path.contains(stub) })
}

tasks.test {
    useJUnitPlatform()
}
