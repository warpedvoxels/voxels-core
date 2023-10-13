plugins {
    alias(libs.plugins.jetpack.compose)
}

allprojects {
    apply(plugin = "org.jetbrains.compose")

    compose {
        kotlinCompilerPlugin.set("1.5.2-beta01")
    }

    dependencies {
        api(compose.runtime) {
            exclude("org.jetbrains.kotlin")
            exclude("org.jetbrains.kotlinx")
        }
    }
}

