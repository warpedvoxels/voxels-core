import net.warpedvoxels.gradle.usePaper

usePaper()

dependencies {
    implementation(project(":voxels-core-architecture"))
    api(project(":voxels-core-networking"))
    api(libs.packetevents.spigot)
}