import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension

plugins {
    id("buildlogic.adapter")
}

dependencies {
   the<PaperweightUserDependenciesExtension>().paperDevBundle("1.21.4-R0.1-SNAPSHOT")
}
