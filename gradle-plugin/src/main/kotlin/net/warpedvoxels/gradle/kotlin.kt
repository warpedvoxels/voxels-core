package net.warpedvoxels.gradle

import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainSpec
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val KOTLIN_ARGS = listOf("-opt-in=kotlin.RequiresOptIn", "-Xcontext-receivers")
private const val JVM_TARGET = 17

internal fun Project.voxelsUseKotlin() = withCatalog("libs") {
    apply(plugin = pluginId("kotlin.jvm"))
    apply(plugin = pluginId("kotlin.serialization"))
    dependencies {
        "implementation"(library("kotlin.serialization"))
        "implementation"(library("kotlin.coroutines"))
        "implementation"(library("kotlin.datetime"))
        "implementation"(kotlin("reflect", version = version("kotlin")))
        "testImplementation"(kotlin("test", version = version("kotlin")))
        "testImplementation"(kotlin("test-junit5", version = version("kotlin")))
        "testImplementation"(library("mockk"))
        "testImplementation"(library("kotest.runner.junit5"))
        "testImplementation"(library("kotest.assertions.core"))
        "testImplementation"(library("kotest.property"))
    }
    plugins.withType<KotlinBasePlugin> {
        extensions.configure<KotlinJvmProjectExtension> {
            jvmToolchain {
                //check(this is JavaToolchainSpec)
                languageVersion.set(JavaLanguageVersion.of(JVM_TARGET))
            }
            explicitApi()
        }
        tasks.withType<KotlinCompile> {
            kotlinOptions.freeCompilerArgs = KOTLIN_ARGS
            kotlinOptions.jvmTarget = "$JVM_TARGET"
        }
    }
}