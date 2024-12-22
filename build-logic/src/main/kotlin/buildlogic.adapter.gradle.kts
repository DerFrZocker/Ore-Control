plugins {
    `java-library`
    id("io.papermc.paperweight.userdev")
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
    mavenCentral()
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION

dependencies {
    "compileOnly"(project(":api"))
    "compileOnly"(project(":common"))
    constraints {
        "remapper"("net.fabricmc:tiny-remapper:[0.8.11,)") {
            because("Need remapper to support Java 21")
        }
    }
}

tasks.named("assemble").configure {
    dependsOn("reobfJar")
}
