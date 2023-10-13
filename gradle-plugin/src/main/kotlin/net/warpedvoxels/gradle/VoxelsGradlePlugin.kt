package net.warpedvoxels.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class VoxelsGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.voxelsUseKotlin()
    }
}