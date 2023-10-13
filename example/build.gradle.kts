import net.warpedvoxels.gradle.codemc
import net.warpedvoxels.gradle.minecraftLibraries
import net.warpedvoxels.gradle.useNetworkingSoftDependencies
import net.warpedvoxels.gradle.usePaper

kotlin {
    explicitApi = null
}

repositories {
    codemc()
    minecraftLibraries()
}

usePaper {
    name = "voxels-core"
    description = "Kotlin developer-friendly library for Minecraft core features and injecting into networking code."
    main = "net.warpedvoxels.core.example.ExampleCorePlugin"
    apiVersion = "1.20"
    useNetworkingSoftDependencies()
}

dependencies {
    api(project(":voxels-core-architecture"))
    api(project(":voxels-core-utility"))
    api(project(":voxels-core-commands:voxels-core-commands-paper"))
    api(project(":voxels-core-networking:voxels-core-networking-paper"))
}
