plugins {
    `java-library`
    id("buildlogic.common")
}

dependencies {
    compileOnly(libs.paperApi)
    implementation(project(":api"))
    implementation(libs.bstats)
    implementation(libs.spigotutils)
    compileOnly(libs.exp4j)
}
