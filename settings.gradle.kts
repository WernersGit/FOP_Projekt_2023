dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
        mavenCentral()
    }
}

pluginManagement {
    repositories {
        mavenLocal()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "FOP-2223-Projekt-Student"
include("application")
include("domain")
include("infrastructure")
