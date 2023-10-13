package net.warpedvoxels.core.example

import net.warpedvoxels.core.architecture.VoxelsPlugin
import net.warpedvoxels.core.command.paper.CommandSuggestions
import net.warpedvoxels.core.command.paper.commands
import net.warpedvoxels.core.example.command.HelpCommand
import net.warpedvoxels.core.example.command.PingCommand
import net.warpedvoxels.core.example.listener.AccuratePing
import net.warpedvoxels.core.networking.invoke
import net.warpedvoxels.core.networking.paper.PacketEvents
import net.warpedvoxels.core.utility.extension.listeners

class ExampleCorePlugin : VoxelsPlugin("example") {
    val networkInjectionPipeline by PacketEvents()

    override suspend fun enable() {
        listeners {
            install(CommandSuggestions)
        }
        networkInjectionPipeline {
            install(*AccuratePing)
        }
        commands {
            install(HelpCommand, PingCommand)
        }
    }
}