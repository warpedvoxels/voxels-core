import net.warpedvoxels.gradle.usePaper

dependencies {
    implementation(project(":voxels-core-utility"))
    implementation(libs.kotlin.serialization.json)
    implementation(libs.bundles.ktor.client)
    implementation(libs.ktor.io)
}