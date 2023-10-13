import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm")
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
}