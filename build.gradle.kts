plugins {
    java
    application

    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("xyz.jpenilla.run-paper") version "2.1.0"
}

group = "net.slqmy"
version = "0.1"
description = "An experimental plugin designed to explore the mechanics of Minecraft servers, debug and test features."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(files("../TSS-Core/build/libs/tss_core-0.1-dev-all.jar"))

    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()

        val props = mapOf(
                "name" to project.name,
                "version" to project.version,
                "description" to project.description,
                "apiVersion" to "1.20"
        )

        inputs.properties(props)

        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    runServer {
        minecraftVersion("1.20.1")
    }
}

application {
    mainClass.set("TSSSlimeLabsPlugin")
}
