import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension

plugins {
    `java-library`
    id("buildlogic.adapter")
}

dependencies {
   the<PaperweightUserDependenciesExtension>().paperDevBundle("1.20.6-R0.1-SNAPSHOT")
}
