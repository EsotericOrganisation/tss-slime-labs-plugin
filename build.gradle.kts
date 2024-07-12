import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
  `java-library`
  id("io.papermc.paperweight.userdev") version "1.7.1"
  id("xyz.jpenilla.run-paper") version "2.3.0" // Adds runServer and runMojangMappedServer tasks for testing
  id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.1" // Generates plugin.yml based on the Gradle config
}

group = "net.slqmy"
version = "0.1"
description = "An experimental plugin designed to explore the mechanics of Minecraft servers, debug and test features."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

dependencies {
    compileOnly(files("../TSS-Core/build/libs/tss_core-0.1-dev-all.jar"))

    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
}

bukkitPluginYaml {
  main = "net.slqmy.tss_slimelabs.TSSSlimeLabsPlugin"
  load = BukkitPluginYaml.PluginLoadOrder.STARTUP
  authors.add("Slqmy")
  apiVersion = "1.21"
}
