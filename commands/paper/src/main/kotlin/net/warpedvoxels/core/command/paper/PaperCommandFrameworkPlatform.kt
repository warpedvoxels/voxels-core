package net.warpedvoxels.core.command.paper

import net.minecraft.commands.CommandSourceStack
import net.warpedvoxels.core.command.CommandFrameworkPlatform

public object PaperCommandFrameworkPlatform : CommandFrameworkPlatform<CommandSourceStack> {
    override fun hasPermission(sender: CommandSourceStack, permission: String): Boolean =
        sender.bukkitSender.hasPermission(permission)
}