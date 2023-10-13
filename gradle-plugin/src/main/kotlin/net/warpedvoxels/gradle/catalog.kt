package net.warpedvoxels.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Calls the specified function [block] with the root project Gradle version catalog
 * as its receiver.
 */
inline fun Project.withCatalog(
    name: String,
    crossinline block: VersionCatalog.() -> Unit
) {
    val libs = rootProject.extensions.getByType<VersionCatalogsExtension>()
    pluginManager.withPlugin("java") {
        val catalog = libs.named(name)
        catalog.block()
    }
}

/**
 * Finds the version of the specified library [name] in the root project Gradle
 * version catalog.
 */
fun VersionCatalog.version(name: String) =
    findVersion(name).get().displayName

/**
 * Finds the library module of the specified library [id] in the root project Gradle
 * version catalog.
 */
fun VersionCatalog.library(id: String) =
    findLibrary(id).get().get().run {
        "$module:${versionConstraint.displayName}"
    }

/**
 * Finds the plugin ID of the specified plugin [name] in the root project Gradle
 * catalog.
 */
fun VersionCatalog.pluginId(name: String) =
    findPlugin(name).get().get().pluginId