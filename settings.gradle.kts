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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "WearWatchfaces"

// (optional) features)
include(":jetpack:feature:base")
include(":jetpack:feature:editor")
include(":jetpack:feature:hands")

// actual watchfaces
include(":jetpack:watchfaces:base")
include(":jetpack:watchfaces:base_analog")
// :jetpack:watchfaces:base_digital goes here

include(":jetpack:watchfaces:dots")

// WatchFaceFormat based watchfaces
include(":wff:jimball")
