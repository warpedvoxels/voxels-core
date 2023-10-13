@file:JvmName("PacketEventsSpigotDsl")

package net.warpedvoxels.core.networking.velocity

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.PacketEventsAPI
import io.github.retrooper.packetevents.velocity.factory.VelocityPacketEventsBuilder
import net.warpedvoxels.proxy.core.PluginLifecycleDelegatedProperty
import net.warpedvoxels.proxy.core.VelocityModule
import net.warpedvoxels.proxy.core.lifecycle

public typealias PacketEventsProperty = PluginLifecycleDelegatedProperty<PacketEventsAPI<*>>

/**
 * Handles the lifecycle of the [`packetevents`](https://github.com/retrooper/packetevents)
 * module on Velocity-based platforms.
 */
public fun VelocityModule.PacketEvents(
    priority: Int = -1,
    debugging: Boolean = false,
    bStats: Boolean = false
): PacketEventsProperty = lifecycle(
    priority,
    init = {
        PacketEvents.setAPI(VelocityPacketEventsBuilder.build(proxyServer, pluginContainer))
        PacketEvents.getAPI().apply {
            settings.bStats(bStats).debug(debugging).checkForUpdates(false)
            load()
            init()
        }
    },
    finalise = {
        PacketEvents.getAPI().terminate()
    },
)