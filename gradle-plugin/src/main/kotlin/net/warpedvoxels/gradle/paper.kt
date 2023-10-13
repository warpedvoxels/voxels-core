package net.warpedvoxels.gradle

import net.minecrell.pluginyml.GeneratePluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

/** Applies the required soft dependencies for networking injection capabilities. */
fun PaperPluginDescription.useNetworkingSoftDependencies() {
    serverDependencies {
        sequenceOf(
            "ProtocolLib", "ProtocolSupport", "ViaVersion", "ViaBackwards",
            "ViaRewind", "Geyser-Spigot"
        ).forEach {
            register(it) { required = false }
        }
    }
}

/**
 * Applies the specified plugin [name] as a core dependency of this Paper plugin.
 */
fun PaperPluginDescription.dependency(name: String, required: Boolean = true) {
    bootstrapDependencies {
        register(name) {
            this.required = required
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
    serverDependencies {
        register(name) {
            this.required = required
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

/**
 * Applies Paper server software dependency with Mojang's mappings and
 * required Paper plugin information to the current project.
 * @param version The version of Paper to use. If null, the latest version is used.
 * @param yml A lambda to configure the plugin.yml file definition.
 */
fun Project.usePaper(version: String? = null, yml: (PaperPluginDescription.() -> Unit)? = null) {
    withCatalog("libs") {
        apply(plugin = pluginId("shadow"))
        apply(plugin = pluginId("paperweight.userdev"))
        apply(plugin = pluginId("run-paper"))
        repositories {
            papermc()
        }
        dependencies {
            if (version == null) {
                "paperweightDevelopmentBundle"(library("paper.dev.bundle"))
            } else {
                val module = findLibrary("paper.dev.bundle").get().get().run {
                    "$module:$version"
                }
                "paperweightDevelopmentBundle"(module)
            }
        }
        tasks.getByName("assemble") {
            dependsOn(tasks.getByName("reobfJar"))
        }
        if (yml != null) {
            apply(plugin = pluginId("plugin.yml"))
            tasks.named<GeneratePluginDescription>("generatePaperPluginDescription") {
                (pluginDescription.get() as PaperPluginDescription?)?.yml()
            }
        }
    }
}