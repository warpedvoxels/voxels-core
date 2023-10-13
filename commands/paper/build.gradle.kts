import net.warpedvoxels.gradle.usePaper

usePaper()

dependencies {
    api(project(":voxels-core-commands"))
    implementation(project(":voxels-core-architecture"))
    implementation(project(":voxels-core-utility"))
}