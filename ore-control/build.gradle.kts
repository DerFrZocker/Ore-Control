import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import io.papermc.paperweight.userdev.attribute.Obfuscation

plugins {
    `java-library`
    id("buildlogic.common")
    id("buildlogic.shadow")
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

val adapters = configurations.create("adapters") {
    description = "Adapters to include in the JAR"
    isCanBeConsumed = false
    isCanBeResolved = true
    shouldResolveConsistentlyWith(configurations["runtimeClasspath"])
    attributes {
        attribute(Obfuscation.OBFUSCATION_ATTRIBUTE, objects.named(Obfuscation.OBFUSCATED))
    }
}

bukkit {
    name = rootProject.name
    version = rootProject.version.toString()
    description = "An ore control plugin"
    author = "DerFrZocker"
    main = "de.derfrzocker.ore.control.OreControl"
    apiVersion = "1.13"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    contributors = listOf("Joo200")
    libraries = listOf("net.objecthunter:exp4j:0.4.8")
    commands {
        register("ore-control") {
            permission = "ore.control"
            description = "Open the ore control gui"
            usage = "Usage: /<command>"
        }
    }
}

dependencies {
    compileOnly(libs.paperApi)
    "implementation"(project(":api"))
    "implementation"(project(":common"))
    "implementation"(libs.spigotutils)
    "implementation"(libs.bstats)
    project.project(":impl").subprojects.forEach {
        "compileOnly"(project(it.path))
        "adapters"(project(it.path))
    }

    "testImplementation"(libs.paperApi)
    "testImplementation"(libs.junit)
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("dist")

    configurations.add(adapters)

    relocate("org.bstats", "de.derfrzocker.ore.control.utils")
    relocate("de.derfrzocker.spigot.utils", "de.derfrzocker.ore.control.utils")
    relocate("net.wesjd", "de.derfrzocker.ore.control.utils")

    exclude("GradleStart**")
    exclude(".cache")
    exclude("LICENSE*")
    exclude("META-INF/maven/**")

    dependencies {
        include(project(":api"))
        include(project(":common"))
        include(dependency("org.bstats:"))
        include(dependency("de.derfrzocker:spigot-utils"))
    }

    project.project(":impl").subprojects.forEach {
        dependencies {
            include(dependency("${it.group}:${it.name}"))
        }
    }
}

tasks.named("assemble").configure {
    dependsOn("shadowJar")
}
