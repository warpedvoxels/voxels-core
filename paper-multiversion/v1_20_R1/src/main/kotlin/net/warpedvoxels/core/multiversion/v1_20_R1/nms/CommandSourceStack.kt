package net.warpedvoxels.core.multiversion.v1_20_R1.nms

import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import net.minecraft.commands.CommandSourceStack as MinecraftCommandSourceStack
import net.warpedvoxels.core.multiversion.api.nms.CommandSourceStack as CommandSourceStackApi

@JvmInline
public value class CommandSourceStack(public val minecraft: MinecraftCommandSourceStack): CommandSourceStackApi {
    override val bukkitSender: CommandSender get() = minecraft.bukkitSender

    override val bukkitEntity: Entity? get() = minecraft.bukkitEntity
}