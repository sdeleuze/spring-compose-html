pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://repo.spring.io/milestone")
        maven("https://packages.jetbrains.team/maven/p/cmp/dev") {
            content {
                includeGroupByRegex("org\\.jetbrains\\.compose\\.html.*")
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.spring.io/milestone")
        google()
        maven("https://packages.jetbrains.team/maven/p/cmp/dev") {
            content {
                includeGroupByRegex("org\\.jetbrains\\.compose\\.html.*")
            }
        }
    }
}

rootProject.name = "spring-compose-html-build"

include("spring-compose-html")
include("examples:hydration-counter:shared")
include("examples:hydration-counter:frontend")
include("examples:hydration-counter:server")
include("examples:hydration-islands:shared")
include("examples:hydration-islands:frontend")
include("examples:hydration-islands:server")
include("examples:ssr-todo")
