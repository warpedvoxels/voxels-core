package net.warpedvoxels.core.command

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future
import kotlin.reflect.KProperty

public typealias ArgumentGetter<S, T> = CommandContext<S>.(label: String) -> T

public typealias SuggestionsProvider<S> = suspend CommandContext<S>.
    (builder: SuggestionsBuilder) -> Suggestions


/**
 * @param S The command source type; who can execute a command
 *          with this argument.
 * @param T Kotlin type for the command argument.
 * @param B Brigadier argument type for this wrapper. In other
 *          words, you receive type [B] and it needs to be
 *          transformed to [T].
 */
public sealed class CommandArgument<S, T, B> {
    /** The name for this argument. */
    public abstract val name: String

    /** Brigadier argument type for this wrapper. */
    public abstract val type: ArgumentType<B>

    /** Turns this data into a Brigadier argument builder. */
    public abstract val brigadier: RequiredArgumentBuilder<S, B>

    /** Provides command suggestions. */
    public abstract val suggests: SuggestionsProvider<S>?

    /**
     * Get the argument value in the given command [context].
     * @param context The context to get the argument value in.
     * @return the argument value.
     */
    public abstract operator fun get(
        context: CommandContext<S>,
        prop: KProperty<*>? = null
    ): T


    /** A mandatory command argument.
     * @param S The command source type; who can execute a command
     *          with this argument.
     * @param T Kotlin type for the command argument.
     * @param B Brigadier argument type for this wrapper. In other
     *          words, you receive type [B] and it needs to be
     *          transformed to [T].
     */
    public data class Required<S, T, B>(
        override val name: String,
        override val type: ArgumentType<B>,
        val coroutineScope: CoroutineScope,
        override val suggests: SuggestionsProvider<S>? = null,
        private val getter: ArgumentGetter<S, T>,
    ) : CommandArgument<S, T, B>() {
        @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
        override val brigadier: RequiredArgumentBuilder<S, B>
            get() = RequiredArgumentBuilder.argument<S, B>(name, type)
                .also {
                    if (suggests != null)
                        it.suggests { context, builder ->
                            coroutineScope.future {
                                suggests!!(context, builder)
                            }
                        }
                }

        override fun get(context: CommandContext<S>, prop: KProperty<*>?): T =
            getter(context, name)
    }

    /** An optional command argument.
     * @param S The command source type; who can execute a command
     *          with this argument.
     * @param T Kotlin type for the command argument.
     * @param B Brigadier argument type for this wrapper. In other
     *          words, you receive type [B] and it needs to be
     *          transformed to [T].
     */
    public data class Optional<S, T, B>(
        val data: CommandArgument<S, T, B>,
        val default: ArgumentGetter<S, T?> = { null },
    ) : CommandArgument<S, T?, B>() {
        override val type: ArgumentType<B>
            get() = data.type
        override val suggests: SuggestionsProvider<S>?
            get() = data.suggests
        override val name: String
            get() = data.name
        override val brigadier: RequiredArgumentBuilder<S, B>
            get() = data.brigadier

        override fun get(context: CommandContext<S>, prop: KProperty<*>?): T? =
            runCatching { data[context, prop] }
                .getOrElse { default(context, name) }
    }
}
