pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Dragon Launcher"
include(":app")

include(":core:ui:main")
include(":core:ui:dragon")
include(":core:ui:base")

include(":core:common")
include(":core:settings")
include(":core:enumsui")
include(":core:models")
include(":core:services")
include(":core:base")
include(":core:shizuku")
include(":core:logging")
