pluginManagement {
    repositories {
        google()
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

rootProject.name = "LichViet"

include(":app")
include(":core:lunar")
include(":core:data")
include(":core:ui")
include(":feature:widget")
include(":feature:reminder")
include(":feature:tools")
include(":feature:settings")
