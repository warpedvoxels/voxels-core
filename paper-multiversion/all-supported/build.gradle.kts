import net.warpedvoxels.gradle.usePaper

usePaper()

dependencies {
    api(project(":voxels-core-paper-multiversion:voxels-core-paper-multiversion-common-api"))
    api(project(":voxels-core-paper-multiversion:voxels-core-paper-multiversion-v1_20_R1"))
}