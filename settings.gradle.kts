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
include(":wear:jetpack:feature:editor")
include(":wear:jetpack:feature:background")
include(":wear:jetpack:feature:hands")
include(":wear:jetpack:feature:cell_grid")
include(":wear:jetpack:feature:digital_clock")

// actual watchfaces
include(":wear:jetpack:watchfaces:base")
include(":wear:jetpack:watchfaces:base_analog")
include(":wear:jetpack:watchfaces:base_digital")

include(":wear:jetpack:watchfaces:dots")
include(":wear:jetpack:watchfaces:jimball")
include(":wear:jetpack:watchfaces:snake")
include(":wear:jetpack:watchfaces:miss_minutes")

// WatchFaceFormat based watchfaces
include(":wear:wff:jimball")
