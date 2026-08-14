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

rootProject.name = "UniCalculator"
include(":app")
include(":core:common")
include(":core:model")
include(":core:math-engine")
include(":core:database")
include(":core:designsystem")
include(":feature:calculator")
include(":feature:cash-tally")
include(":feature:business-tools")
include(":feature:history-tape")
