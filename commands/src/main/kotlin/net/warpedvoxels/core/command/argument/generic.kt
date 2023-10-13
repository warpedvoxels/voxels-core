@file:JvmName("GenericArgument")

package net.warpedvoxels.core.command.argument

import com.mojang.brigadier.arguments.ArgumentType
import kotlinx.coroutines.future.await
import net.warpedvoxels.core.command.*

/**
 * Models a **voxels-core** command argument after a Brigadier argument.
 *
 * @param name The name of this argument.
 * @param type The type of this argument.
 * @param suggestions The suggestions for this argument.
 * @param getter The getter for this argument.
 * @param S The type of the command sender.
 *
 * ## Examples
 *
 * **Word argument type**
 * ```kotlin
 * argument(name, type, StringArgumentType::getString, StringArgumentType.word()) {
 *     suggest("Hello")
 *     suggest("World")
 * }
 * ```
 */
@CommandDslMarker
public fun <S, T : Any?, B> UsesCommandTree<S>.argument(
    name: String,
    type: ArgumentType<B>,
    getter: ArgumentGetter<S, T>,
    suggestions: SuggestionsDsl<S>? = null,
): CommandArgument<S, T, B> = CommandArgument.Required(
    name, type, tree.coroutineScope,
    if (suggestions == null) null else { builder ->
        val dsl = SuggestionsBuilderDsl(this, builder)
        dsl.suggestions()
        builder.buildFuture().await()
    }, getter
)

/**
 * Creates an optional variant of this command argument. Works by skipping
 * errors and providing default solutions for them.
 *
 * @param default The default value for this property.
 */
context(BrigadierCommandDsl<S>)
@CommandDslMarker
public fun <S, T, B> CommandArgument<S, T, B>.optional(
    default: ArgumentGetter<S, T?> = { null },
): CommandArgument<S, T?, B> =
    CommandArgument.Optional(this, default)

/**
 *
 * @param default The default value for this property.
 */
context(BrigadierCommandDsl<S>)
@Suppress("UNCHECKED_CAST")
@CommandDslMarker
public fun <S, T, B> CommandArgument<S, T, B>.default(
    default: ArgumentGetter<S, T>,
): CommandArgument<S, T, B> =
    CommandArgument.Optional(this, default) as CommandArgument<S, T, B>
