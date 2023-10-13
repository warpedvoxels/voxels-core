@file:Suppress("SpellCheckingInspection")
@file:JvmName("CommandExtensions")

package net.warpedvoxels.core.command

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

public const val Success: Int = 1
public const val Failure: Int = -1

/** An exception that cancels the flow of a command. */
public data object CommandCancelFlowException :
    Exception(null, null, false, false) {
    private fun readResolve(): Any = CommandCancelFlowException
    override fun fillInStackTrace(): Throwable {
        return this
    }
}

@Suppress("NOTHING_TO_INLINE")
private inline fun baseThrow(key: String): Nothing {
    val component = Component.translatable(key)
    val plain = PlainTextComponentSerializer.plainText()
        .serialize(component)
    val type = SimpleCommandExceptionType { plain }
    throw type.create()
}

/**
 * Throws a built-in exception that indicates no players were
 * found.
 */
public fun noPlayersFound(): Nothing =
    baseThrow("argument.entity.notfound.player")

/**
 * Throws a built-in exception that indicates no entities were
 * found.
 */
public fun noEntitiesFound(): Nothing =
    baseThrow("argument.entity.notfound.entity")

/**
 * Throws a built-in exception that indicates the given selector
 * is not allowed.
 */
public fun selectorNotAllowed(): Nothing =
    baseThrow("argument.entity.selector.not_allowed")

/**
 * Throws a built-in exception that indicates only players are
 * accepted as argument.
 */
public fun onlyPlayersAllowed(): Nothing =
    baseThrow("argument.player.entities")

/**
 * Throws a built-in exception that indicates only a *single*
 * player is accepted.
 */
public fun notSinglePlayer(): Nothing =
    baseThrow("argument.player.toomany")

/**
 * Throws a built-in exception that indicates only a *single*
 * entity is accepted.
 */
public fun notSingleEntity(): Nothing =
    baseThrow("argument.entity.toomany")
