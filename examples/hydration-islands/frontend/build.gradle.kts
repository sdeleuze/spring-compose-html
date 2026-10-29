@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

description = "Browser application hydrating the hydration islands rendered by the server"

kotlin {
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "app.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        wasmJsMain.dependencies {
            implementation(project(":examples:hydration-islands:shared"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kotlinx.browser.common.subset)
        }
    }
}

// Expose the browser bundle (app.js and Wasm binaries) so the server can serve it as static resources
val wasmJsBrowserDistribution = tasks.named("wasmJsBrowserDistribution")
val browserDistribution = configurations.consumable("browserDistribution")
artifacts {
    add(browserDistribution.name, layout.buildDirectory.dir("dist/wasmJs/productionExecutable")) {
        builtBy(wasmJsBrowserDistribution)
    }
}
