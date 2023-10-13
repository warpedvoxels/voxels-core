import net.warpedvoxels.gradle.codemc
import net.warpedvoxels.gradle.minecraftLibraries
import net.warpedvoxels.gradle.papermc

plugins {
    id("voxels-gradle-plugin")
    `kotlin-conventions`
    `dokkatoo-conventions`
}

allprojects {
    apply(plugin = "voxels-gradle-plugin")

    repositories {
        mavenCentral()
    }

    tasks {
        jar {
            from(rootProject.file("COPYING.md"))
            from(rootProject.file("COPYING.LESSER"))
        }
        test {
            useJUnitPlatform()
        }
    }
}

repositories {
    papermc()
    codemc()
    minecraftLibraries()
}
