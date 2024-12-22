
plugins {
    `java-library`
    id("buildlogic.common")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.paperApi)
    compileOnly(libs.jetbrainsAnnotations)
}
