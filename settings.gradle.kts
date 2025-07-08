rootProject.name = "keyple-demo-ticketing-control-app"
include (":app")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://nexus.coppernic.fr/repository/libs-release")
        google()
        maven(url = "https://central.sonatype.com/repository/maven-snapshots")
    }
}

