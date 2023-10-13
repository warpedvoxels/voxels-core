package net.warpedvoxels.gradle

import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

fun Project.useVelocity() = withCatalog("libs") {
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = pluginId("shadow"))
    repositories {
        papermc()
    }
    dependencies {
        "compileOnly"(library("velocity.api"))
        "kapt"(library("velocity.api"))
    }
}