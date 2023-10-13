import net.warpedvoxels.gradle.useVelocity

useVelocity()

dependencies {
    implementation(project(":voxels-proxy-core:voxels-proxy-core-architecture"))
    api(project(":voxels-core-networking"))
    api(libs.packetevents.velocity)
}