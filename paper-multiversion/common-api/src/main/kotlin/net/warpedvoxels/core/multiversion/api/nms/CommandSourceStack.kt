package net.warpedvoxels.core.multiversion.api.nms

import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity

public interface CommandSourceStack {
    public val bukkitSender: CommandSender

    public val bukkitEntity: Entity?
}