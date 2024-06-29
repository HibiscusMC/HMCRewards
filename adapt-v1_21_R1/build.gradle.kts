plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")

    api(project(":hmcrewards-adapt-api"))
}