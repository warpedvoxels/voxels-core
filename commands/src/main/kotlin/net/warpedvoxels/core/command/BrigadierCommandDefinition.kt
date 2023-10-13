package net.warpedvoxels.core.command

/**
 * The definition of a Minecraft command using Mojang's Brigadier
 * API.
 *
 * @param names       Each name accepted to perform an execution
 *                    of this command.
 * @param permission  The permission required for players to run
 *                    this command.
 */
public data class BrigadierCommandDefinition(
    val names: List<String>,
    val permission: String?,
)