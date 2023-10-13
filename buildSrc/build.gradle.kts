import org.gradle.kotlin.dsl.support.expectedKotlinDslPluginsVersion

plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven(url = "https://raw.githubusercontent.com/adamko-dev/dokkatoo/artifacts/m2") {
        name = "Dokkatoo Snapshots"
    }
}

dependencies {
    with(libs.plugin) {
        implementation(kotlin.jvm)
        implementation(kotlin.serialization)
        implementation(dokkatoo)
        implementation(shadow)
        implementation(plugin.yml)
        implementation(run.task)
        implementation(paperweight.userdev)
    }
}