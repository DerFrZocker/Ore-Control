import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension

plugins {
    `java-library`
    id("buildlogic.adapter")
}

dependencies {
   the<PaperweightUserDependenciesExtension>().paperDevBundle("1.21.3-R0.1-20241203.150031-85")
}
