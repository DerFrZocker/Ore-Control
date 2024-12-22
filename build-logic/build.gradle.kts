plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())
    implementation(libs.grgit)
    implementation(libs.paperweight)
    implementation(libs.shadow)
}
