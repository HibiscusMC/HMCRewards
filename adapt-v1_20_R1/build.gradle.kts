plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")

    api(project(":hmcrewards-adapt-api"))
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}

tasks {

    compileJava {
        options.release = 17
    }
}