pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            name = "Paper"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
    }
}
dependencyResolutionManagement {
    repositories {
        maven {
            name = "Paper"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
    }
}

rootProject.name = "ore-control"

includeBuild("build-logic")

include("api")
include("common")
include("ore-control")

listOf(
    "v1_20_R2",
    "v1_20_R3",
    "v1_20_R4",
    "v1_21_R1",
    "v1_21_R2",
    "v1_21_R3"
).forEach {
    include("impl:$it")
}
