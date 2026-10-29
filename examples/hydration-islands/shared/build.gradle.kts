@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

description = "Hydration islands UI shared by the server (JVM) and the frontend (Wasm)"

kotlin {
    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_25
        }
    }
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kotlinx.browser.common.subset)
        }
    }
}

