plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "HMCRewards"

includePrefixed("adapt-api")
includePrefixed("adapt-v1_20_R1")
includePrefixed("adapt-v1_20_R4")
includePrefixed("adapt-v1_21_R1")

fun includePrefixed(name: String) {
    val kebabName = name.replace(':', '-')
    val path = name.replace(':', '/')

    include("hmcrewards-$kebabName")
    project(":hmcrewards-$kebabName").projectDir = file(path)
}