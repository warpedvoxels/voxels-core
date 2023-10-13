import net.warpedvoxels.gradle.codemc

kotlin {
    explicitApi = null
}

repositories {
    codemc()
}

dependencies {
    api(project(":voxels-core-networking:voxels-core-networking-velocity"))
    api(project(":voxels-core-commands:voxels-core-commands-velocity"))
    api(project(":voxels-proxy-core:voxels-proxy-core-architecture"))
    api(project(":voxels-proxy-core:voxels-proxy-core-utility"))
}