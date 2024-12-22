import org.gradle.kotlin.dsl.`java-library`
import org.gradle.kotlin.dsl.repositories

plugins {
    `java-library`
    id("buildlogic.common-java")
}

repositories {
    maven {
        name = "Paper"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "DerFrZocker-Releases"
        url = uri("https://nexus.derfrzocker.de/repository/maven-releases/")
        metadataSources {
            artifact()
        }
    }
    maven {
        name = "CodeMC"
        url = uri("https://repo.codemc.org/repository/maven-public")
    }
    mavenCentral()
}
