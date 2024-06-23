plugins {
    java
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("io.github.goooler.shadow") version "8.1.7"
}

repositories {
    mavenLocal()
    maven("https://libraries.minecraft.net/") // datafixerupper
    maven("https://papermc.io/repo/repository/maven-public/") // paper-api, paperlib
    maven("https://repo.oraxen.com/releases") // Oraxen
    maven("https://repo.unnamed.team/repository/unnamed-public/") // command-flow
    maven("https://repo.hibiscusmc.com/releases/") // HibiscusCommons
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // PlaceholderAPI
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") // adventure-platform-bukkit
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")

    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly("io.th0rgal:oraxen:1.164.0") // Oraxen
    compileOnly("me.lojosho:HibiscusCommons:0.2.4") // HibiscusCommons
    compileOnly("me.clip:placeholderapi:2.11.5") // PlaceholderAPI
    compileOnly("com.mojang:datafixerupper:6.0.8") // datafixerupper

    implementation("team.unnamed:inject:2.0.1") // inject
    implementation("dev.triumphteam:triumph-gui:3.1.8") // triumph-gui
    implementation("net.kyori:adventure-text-serializer-bungeecord:4.3.2") // adventure-text-serializer-bungeecord
    implementation("xyz.jpenilla:reflection-remapper:0.1.0") // reflection-remapper
    implementation("io.papermc:paperlib:1.0.8") // paperlib

    implementation("org.mongodb:mongodb-driver-sync:4.11.1") // mongodb driver

    implementation("me.fixeddev:commandflow-universal:0.6.0")
    implementation("me.fixeddev:commandflow-bukkit:0.6.0")

    implementation(project(":hmcrewards-adapt-api"))
    implementation(project(":hmcrewards-adapt-v1_20_R1", configuration = "reobf"))
    implementation(project(":hmcrewards-adapt-v1_20_R4", configuration = "reobf"))

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    runServer {
        downloadPlugins {
            github("dmulloy2", "ProtocolLib", "5.1.0", "ProtocolLib.jar")
            url("https://repo.hibiscusmc.com/releases/me/lojosho/HibiscusCommons/0.4.2/HibiscusCommons-0.4.2-all.jar")
        }
        minecraftVersion("1.20.1")
    }
    test {
        useJUnitPlatform()
    }
    processResources {
        filesMatching("plugin.yml") {
            expand("project" to project)
        }
    }
    compileJava {
        options.release = 17
    }
    shadowJar {
        val pkg = "com.hibiscusmc.hmcrewards.lib"
        relocate("team.unnamed.inject", "$pkg.inject")
        relocate("me.fixeddev.commandflow", "$pkg.commandflow")
        relocate("dev.triumphteam.gui", "$pkg.triumphgui")
        relocate("com.mongodb", "$pkg.mongodb")
        relocate("org.bson", "$pkg.bson")
        relocate("xyz.jpenilla.reflectionremapper", "$pkg.reflectionremapper")
        relocate("net.kyori.adventure.nbt", "$pkg.adventure.nbt")
    }
}