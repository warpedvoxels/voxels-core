package net.warpedvoxels.gradle

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.maven

const val PAPERMC_REPOSITORY_URL = "https://papermc.io/repo/repository/maven-public/"
const val MINECRAFT_LIBRARIES_REPOSITORY_URL = "https://libraries.minecraft.net/"
const val CODEMC_REPOSITORY_URL = "https://repo.codemc.io/repository/maven-releases/"

fun RepositoryHandler.papermc() = maven(url = PAPERMC_REPOSITORY_URL) {
    name = "PaperMC"
}

fun RepositoryHandler.minecraftLibraries() = maven(url = MINECRAFT_LIBRARIES_REPOSITORY_URL) {
    name = "Minecraft Libraries"
}

fun RepositoryHandler.codemc() = maven(url = CODEMC_REPOSITORY_URL) {
    name = "CodeMC Releases"
}

