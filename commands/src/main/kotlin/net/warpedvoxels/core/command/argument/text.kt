@file:JvmName("TextArgument")

package net.warpedvoxels.core.command.argument

import com.mojang.brigadier.arguments.StringArgumentType
import net.warpedvoxels.core.command.CommandArgument
import net.warpedvoxels.core.command.UsesCommandTree
import net.warpedvoxels.core.command.CommandDslMarker
import net.warpedvoxels.core.command.SuggestionsDsl

/**
 * Creates a new [CommandArgument] of type [String] with the given [name] and
 * [suggestions] provider.
 *
 * @param type The type of string argument to create.
 *
 * ## Examples
 *
 * ```kotlin
 * val wordArgument by string("string_arg", StringArgumentType.word())
 * ```
 */
@CommandDslMarker
public fun <S> UsesCommandTree<S>.literal(
    name: String,
    type: StringArgumentType,
    suggestions: SuggestionsDsl<S>? = null
): CommandArgument<S, String, String> =
    argument(name, type, StringArgumentType::getString, suggestions)

/**
 * Creates a new [CommandArgument] of type [String] with the given [name],
 * [suggestions] provider, and of kind [StringArgumentType.word].
 *
 * ## Examples
 *
 * ```kotlin
 * val wordArgument by word("string_arg")
 * ```
 */
@CommandDslMarker
public fun <S> UsesCommandTree<S>.word(
    name: String,
    suggestions: SuggestionsDsl<S>? = null
): CommandArgument<S, String, String> =
    literal(name, StringArgumentType.word(), suggestions)

/**
 * Creates a new [CommandArgument] of type [String] with the given [name],
 * [suggestions] provider, and of kind [StringArgumentType.string].
 *
 * ## Examples
 *
 * ```kotlin
 * val stringArgument by string("string_arg")
 * ```
 */
@CommandDslMarker
public fun <S> UsesCommandTree<S>.string(
    name: String,
    suggestions: SuggestionsDsl<S>? = null
): CommandArgument<S, String, String> =
    literal(name, StringArgumentType.string(), suggestions)

/**
 * Creates a new [CommandArgument] of type [String] with the given [name],
 * [suggestions] provider, and of kind [StringArgumentType.greedyString].
 *
 * ## Examples
 *
 * ```kotlin
 * val greedyStringArgument by greedyString("string_arg")
 * ```
 */
@CommandDslMarker
public fun <S> UsesCommandTree<S>.greedyString(
    name: String,
    suggestions: SuggestionsDsl<S>? = null
): CommandArgument<S, String, String> =
    literal(name, StringArgumentType.greedyString(), suggestions)