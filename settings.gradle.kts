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

// shared watchfaces/features code
include(":wear:jetpack:shared")

// (optional) features)
include(":wear:jetpack:feature:base")
include(":wear:jetpack:feature:editor")
include(":wear:jetpack:feature:overlay")
include(":wear:jetpack:feature:hands")
include(":wear:jetpack:feature:cell_grid")
include(":wear:jetpack:feature:colors")

// actual watchfaces
include(":wear:jetpack:watchfaces:base")
include(":wear:jetpack:watchfaces:base_analog")
include(":wear:jetpack:watchfaces:base_digital")

include(":wear:jetpack:watchfaces:dots")
include(":wear:jetpack:watchfaces:jimball")
include(":wear:jetpack:watchfaces:snake")
include(":wear:jetpack:watchfaces:miss_minutes")
include(":wear:jetpack:watchfaces:nicely_blurry")
include(":wear:jetpack:watchfaces:clean_colorful")
include(":wear:jetpack:watchfaces:birds")
include(":wear:jetpack:watchfaces:new_year")
include(":wear:jetpack:watchfaces:pointy_damascus")

// WatchFaceFormat based watchfaces
include(":wear:wff:jimball")
