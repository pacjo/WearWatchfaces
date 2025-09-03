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
include(":jetpack:feature:editor")
include(":jetpack:feature:background")
include(":jetpack:feature:hands")
include(":jetpack:feature:cell_grid")

// actual watchfaces
include(":jetpack:watchfaces:base")
include(":jetpack:watchfaces:base_analog")
include(":jetpack:watchfaces:base_digital")

include(":jetpack:watchfaces:dots")
include(":jetpack:watchfaces:jimball")
include(":jetpack:watchfaces:snake")
include(":jetpack:watchfaces:miss_minutes")

// WatchFaceFormat based watchfaces
include(":wff:jimball")
