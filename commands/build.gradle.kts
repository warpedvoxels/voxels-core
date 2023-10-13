import net.warpedvoxels.gradle.minecraftLibraries
import net.warpedvoxels.gradle.papermc

allprojects {
    repositories {
        papermc()
        minecraftLibraries()
    }
}

dependencies {
    compileOnly(libs.adventure.api)
    compileOnly(libs.adventure.text.serializer.plain)
    compileOnly(libs.brigadier)
}
