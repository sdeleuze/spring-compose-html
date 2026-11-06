import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.html.lowering) // optional, lower static parts at compile time for faster rendering
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
}

description = "Spring Boot server rendering the counter page"

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
    developmentOnly(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.kotlin.bom))
    implementation(project(":spring-compose-html-webmvc"))
    implementation(project(":examples:hydration-counter:shared"))
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    browserDistribution.name(project(path = ":examples:hydration-counter:frontend", configuration = "browserDistribution"))

    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.processResources {
    from(browserDistributionFiles) {
        into("static")
    }
}

// Run the application with the shared module classes directories (resolved for compilation, thanks to
// kotlin.jvm.addClassesVariant=true in gradle.properties) rather than its jar, so that Spring Boot devtools
// restarts it when they are updated
tasks.bootRun {
    val isShared = { id: ComponentIdentifier -> id is ProjectComponentIdentifier && id.projectPath == ":examples:hydration-counter:shared" }
    classpath = sourceSets.main.get().output +
        configurations.compileClasspath.get().incoming.artifactView { componentFilter(isShared) }.files +
        configurations.runtimeClasspath.get().incoming.artifactView { componentFilter { !isShared(it) } }.files
}

tasks.test {
    useJUnitPlatform()
}
