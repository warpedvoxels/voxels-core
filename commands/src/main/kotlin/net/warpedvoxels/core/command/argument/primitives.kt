@file:JvmName("NumberArgument")

package net.warpedvoxels.core.command.argument

import com.mojang.brigadier.arguments.*
import net.warpedvoxels.core.command.CommandArgument
import net.warpedvoxels.core.command.UsesCommandTree
import net.warpedvoxels.core.command.CommandDslMarker
import net.warpedvoxels.core.command.SuggestionsDsl

/**
 * Creates a new [CommandArgument] of type [Int] with the given [name] and
 * [suggestions] provider.
 *
 * @param min The minimum value the argument range accepts.
 * @param max The maximum value the argument range accepts.
 *
 * ## Examples
 *
 * ```kotlin
 * val myIntegerArgument by integer("int_arg", 0, 10)
 * ```
 */
@CommandDslMarker
public fun <S> UsesCommandTree<S>.integer(
    name: String,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
    suggestions: SuggestionsDsl<S>? = null
): CommandArgument<S, Int, Int> = argument(
    name,
    IntegerArgumentType.integer(min, max),
    IntegerArgumentType::getInteger,
    suggestions
)

/**
 * Creates a new [CommandArgument] of type [Long] with the given [name] and
 * [suggestions] provider.
 *
 * @param min The minimum value the argument range accepts.
 * @param max The maximum value the argument range accepts.
 *
 * ## Examples
 *
 * ```kotlin
 * val myLongArgument by long("long_arg", 0, 10)
 * ```
 */
@CommandDslMarker
public fun <S> UsesCommandTree<S>.long(
    name: String,
    min: Long = Long.MIN_VALUE,
    max: Long = Long.MAX_VALUE,
    suggestions: SuggestionsDsl<S>? = null
): CommandArgument<S, Long, Long> = argument(
    name,
    LongArgumentType.longArg(min, max),
    LongArgumentType::getLong,
    suggestions
)

/**
 * Creates a new [CommandArgument] of type [Double] with the given [name] and
 * [suggestions] provider.
 *
 * @param min The minimum value the argument range accepts.
 * @param max The maximum value the argument range accepts.
 *
 * ## Examples
 *
 * ```kotlin
 * val myDoubleArgument by double("double_arg", 0.0, 10.0)
 * ```
 */
@CommandDslMarker
public fun <S> UsesCommandTree<S>.double(
    name: String,
    min: Double = Double.MIN_VALUE,
    max: Double = Double.MAX_VALUE,
    suggestions: SuggestionsDsl<S>? = null
): CommandArgument<S, Double, Double> = argument(
    name,
    DoubleArgumentType.doubleArg(min, max),
    DoubleArgumentType::getDouble,
    suggestions
)

/**
 * Creates a new [CommandArgument] of type [Float] with the given [name] and
 * [suggestions] provider.
 *
 * @param min The minimum value the argument range accepts.
 * @param max The maximum value the argument range accepts.
 *
 * ## Examples
 *
 * ```kotlin
 * val myFloatArgument by float("float_arg", 0.0f, 10.0f)
 * ```
 */
@CommandDslMarker
public fun <S> UsesCommandTree<S>.float(
    name: String,
    min: Float = Float.MIN_VALUE,
    max: Float = Float.MAX_VALUE,
    suggestions: SuggestionsDsl<S>? = null
): CommandArgument<S, Float, Float> = argument(
    name,
    FloatArgumentType.floatArg(min, max),
    FloatArgumentType::getFloat,
    suggestions
)

/**
 * Creates a new [CommandArgument] of type [Boolean] with the given [name] and
 * [suggestions] provider.
 *
 * ## Examples
 *
 * ```kotlin
 * val myBooleanArgument by bool("bool_arg")
 * ```
 */
@CommandDslMarker
public fun <S> UsesCommandTree<S>.bool(
    name: String,
    suggestions: SuggestionsDsl<S>? = null
): CommandArgument<S, Boolean, Boolean> =
    argument(
        name,
        BoolArgumentType.bool(),
        BoolArgumentType::getBool,
        suggestions
    )