package net.warpedvoxels.core.command.velocity

import com.velocitypowered.api.command.CommandSource
import net.warpedvoxels.core.command.CommandFrameworkPlatform
import net.warpedvoxels.proxy.core.VelocityModule

@JvmInline
public value class VelocityCommandFrameworkPlatform(
    public val extension: VelocityModule
) : CommandFrameworkPlatform<CommandSource> {
    override fun hasPermission(sender: CommandSource, permission: String): Boolean =
        sender.hasPermission(permission)
}