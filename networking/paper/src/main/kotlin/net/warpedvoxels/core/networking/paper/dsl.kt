@file:JvmName("PacketEventsSpigotDsl")

package net.warpedvoxels.core.networking.paper

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.PacketEventsAPI
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import net.warpedvoxels.core.architecture.PluginLifecycleDelegatedProperty
import net.warpedvoxels.core.architecture.VoxelsPlugin
import net.warpedvoxels.core.architecture.lifecycle

/**
 * Handles the lifecycle of the
 * [`packetevents`](https://github.com/retrooper/packetevents) module on
 * Spigot-based platforms.
 *
 * ## Examples
 *
 * ```kotlin
 * val networkInjectionPipeline by PacketEvents()
 * ```
 */
public fun VoxelsPlugin.PacketEvents(
    priority: Int = -1,
    debugging: Boolean = false,
    bStats: Boolean = false,
): PluginLifecycleDelegatedProperty<PacketEventsAPI<*>> = lifecycle(
    priority,
    load = {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
        PacketEvents.getAPI().apply {
            settings.bStats(bStats).debug(debugging).checkForUpdates(false)
            load()
        }
    },
    enable = {
        PacketEvents.getAPI().apply { init() }
    },
    disable = { PacketEvents.getAPI().terminate() },
)