@file:JvmName("PaperEntityArgument")

package net.warpedvoxels.core.command.paper.argument

import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.selector.EntitySelector
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.warpedvoxels.core.command.CommandArgument
import net.warpedvoxels.core.command.UsesCommandTree
import net.warpedvoxels.core.command.CommandDslMarker
import net.warpedvoxels.core.command.SuggestionsDsl
import net.warpedvoxels.core.command.argument.argument
import org.bukkit.entity.Player

/**
 * Creates a new [CommandArgument] of type [Entity] with the given [name],
 * [suggestions] provider, and of kind [EntityArgument.entity].
 *
 * ## Examples
 *
 * ```kotlin
 * val entityArgument by entity("entity_arg")
 * ```
 */
@CommandDslMarker
public fun UsesCommandTree<CommandSourceStack>.entity(
    name: String,
    suggestions: SuggestionsDsl<CommandSourceStack>? = null
): CommandArgument<CommandSourceStack, Entity, EntitySelector> =
    argument(
        name,
        EntityArgument.entity(),
        EntityArgument::getEntity,
        suggestions
    )

/**
 * Creates a new [CommandArgument] of type [MutableCollection] of [Entity] with
 * the given [name], [suggestions] provider, and of kind [EntityArgument.entities].
 *
 * ## Examples
 *
 * ```kotlin
 * val entitiesArgument by entities("entities_arg")
 * ```
 */
@CommandDslMarker
public fun UsesCommandTree<CommandSourceStack>.entities(
    name: String,
    suggestions: SuggestionsDsl<CommandSourceStack>? = null
): CommandArgument<CommandSourceStack, MutableCollection<out Entity>, EntitySelector> =
    argument(
        name,
        EntityArgument.entities(),
        EntityArgument::getEntities,
        suggestions
    )

/**
 * Creates a new [CommandArgument] of type [Player] with the given [name],
 * [suggestions] provider, and of kind [EntityArgument.player].
 *
 * ## Examples
 *
 * ```kotlin
 * val playerArgument by player("player_arg")
 */
@CommandDslMarker
public fun UsesCommandTree<CommandSourceStack>.player(
    name: String,
    suggestions: SuggestionsDsl<CommandSourceStack>? = null
): CommandArgument<CommandSourceStack, Player, EntitySelector> =
    argument(
        name,
        EntityArgument.player(),
        { EntityArgument.getPlayer(this, it).bukkitEntity },
        suggestions
    )

/**
 * Creates a new [CommandArgument] of type [List] of [Player] with the given
 * [name], [suggestions] provider, and of kind [EntityArgument.players].
 *
 * ## Examples
 *
 * ```kotlin
 * val playersArgument by players("players_arg")
 * ```
 */
@CommandDslMarker
public fun UsesCommandTree<CommandSourceStack>.players(
    name: String,
    suggestions: SuggestionsDsl<CommandSourceStack>? = null
): CommandArgument<CommandSourceStack, List<Player>, EntitySelector> =
    argument(
        name,
        EntityArgument.players(),
        { EntityArgument.getPlayers(this, it).map(ServerPlayer::getBukkitEntity) },
        suggestions
    )
