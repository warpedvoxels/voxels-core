@file:JvmName("VelocityEntityArgument")

package net.warpedvoxels.core.command.velocity.argument

import com.mojang.brigadier.arguments.StringArgumentType
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import net.warpedvoxels.core.command.*
import net.warpedvoxels.core.command.argument.argument
import net.warpedvoxels.core.command.velocity.VelocityCommandFrameworkPlatform
import kotlin.jvm.optionals.getOrNull

/**
 * Creates a new [CommandArgument] of type [Player] with the given [name],
 * [suggestions] provider.
 *
 * ## Examples
 *
 * ```kotlin
 * val playerArgument by player("player_arg")
 */
@CommandDslMarker
public fun UsesCommandTree<CommandSource>.player(
    name: String,
    suggestions: SuggestionsDsl<CommandSource>? = null,
): CommandArgument<CommandSource, Player, String> {
    val platform = tree.platform as? VelocityCommandFrameworkPlatform ?: error("Unsupported platform for player argument.")
    return argument(
        name,
        StringArgumentType.word(),
        { input ->
            platform.extension.proxyServer.getPlayer(
                StringArgumentType.getString(this, input)
            ).getOrNull() ?: noPlayersFound()
        },
        suggestions
    )
}
