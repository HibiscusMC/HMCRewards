plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenLocal()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.oraxen.com/releases") // Oraxen
    maven("https://repo.unnamed.team/repository/unnamed-public/") // command-flow
    maven("https://repo.hibiscusmc.com/releases/") // HibiscusCommons
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")

    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly("io.th0rgal:oraxen:1.164.0") // Oraxen
    compileOnly("me.lojosho:HibiscusCommons:0.2.4") // HibiscusCommons

    implementation("team.unnamed:inject:2.0.1") // inject
    implementation("dev.triumphteam:triumph-gui:3.1.6") // triumph-gui

    implementation("org.mongodb:mongodb-driver-sync:4.11.1") // mongodb driver

    implementation("me.fixeddev:commandflow-universal:0.6.0")
    implementation("me.fixeddev:commandflow-bukkit:0.6.0")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
    processResources {
        filesMatching("plugin.yml") {
            expand("project" to project)
        }
    }
    shadowJar {
        minimize()

        val pkg = "com.hibiscusmc.hmcrewards.lib"
        relocate("team.unnamed.inject", "$pkg.inject")
        relocate("me.fixeddev.commandflow", "$pkg.commandflow")
        relocate("dev.triumphteam.gui", "$pkg.triumphgui")
    }
}