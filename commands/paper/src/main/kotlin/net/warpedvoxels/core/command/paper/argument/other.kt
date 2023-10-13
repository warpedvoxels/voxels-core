@file:JvmName("PaperExtraArgument")

package net.warpedvoxels.core.command.paper.argument

import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.format.TextColor
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.ColorArgument
import net.warpedvoxels.core.command.CommandArgument
import net.warpedvoxels.core.command.UsesCommandTree
import net.warpedvoxels.core.command.CommandDslMarker
import net.warpedvoxels.core.command.SuggestionsDsl
import net.warpedvoxels.core.command.argument.argument

/**
 * Creates a new [CommandArgument] of type [TextColor] with the given [name],
 * [suggestions] provider, and of kind [ColorArgument.color].
 *
 * ## Examples
 *
 * ```kotlin
 * val colorArgument by color("color_arg")
 * ```
 */
@CommandDslMarker
public fun UsesCommandTree<CommandSourceStack>.color(
    name: String,
    suggestions: SuggestionsDsl<CommandSourceStack>? = null
): CommandArgument<CommandSourceStack, TextColor, ChatFormatting> =
    argument(
        name,
        ColorArgument.color(),
        { label ->
            val minecraft = ColorArgument.getColor(this, label)
            PaperAdventure.asAdventure(minecraft)
        },
        suggestions
    )