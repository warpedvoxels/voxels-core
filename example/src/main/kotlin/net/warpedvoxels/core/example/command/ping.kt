@file:JvmName("PingCommand")

package net.warpedvoxels.core.example.command

import net.warpedvoxels.core.command.argument.default
import net.warpedvoxels.core.command.paper.argument.player
import net.warpedvoxels.core.command.paper.bukkitPlayer
import net.warpedvoxels.core.command.paper.command
import net.warpedvoxels.core.command.paper.player
import net.warpedvoxels.core.command.paper.respond
import net.warpedvoxels.core.example.ExampleCorePlugin
import net.warpedvoxels.core.example.listener.accuratePing
import org.bukkit.entity.Player

internal val ExampleCorePlugin.PingCommand
    get() = command("ping") {
        val source by player("player").default { source.bukkitPlayer }
        executes {
            respond("Accurate ping: ${source.accuratePing}\nAverage ping: ${source.ping}")
        }
    }