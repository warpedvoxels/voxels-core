import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.warpedvoxels.gradle.codemc

allprojects {
    apply(plugin = rootProject.libs.plugins.shadow.get().pluginId)

    repositories {
        codemc()
    }

    tasks {
        named<ShadowJar>("shadowJar") {
            relocate("com.github.retrooper.packetevents", "net.warpedvoxels.packetevents.api")
            relocate("io.github.retrooper.packetevents", "net.warpedvoxels.packetevents.impl")
        }
    }
}

dependencies {
    api(libs.packetevents.api)
}

