@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.plugin.kotlin.jvm)
    implementation(libs.plugin.shadow)
    implementation(libs.plugin.plugin.yml)
}

gradlePlugin {
    plugins.register("voxels-gradle-plugin") {
        id = "voxels-gradle-plugin"
        implementationClass = "net.warpedvoxels.gradle.VoxelsGradlePlugin"
    }
}
