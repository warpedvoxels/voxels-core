@file:JvmName("PaperCoordinatesArgument")

package net.warpedvoxels.core.command.paper.argument

import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.coordinates.Coordinates
import net.minecraft.commands.arguments.coordinates.Vec3Argument
import net.warpedvoxels.core.command.CommandArgument
import net.warpedvoxels.core.command.UsesCommandTree
import net.warpedvoxels.core.command.CommandDslMarker
import net.warpedvoxels.core.command.SuggestionsDsl
import net.warpedvoxels.core.command.argument.argument
import org.bukkit.Location

/**
 * Creates a new [CommandArgument] of type [Location] with the given [name],
 * [suggestions] provider, and of kind [Vec3Argument.vec3] (three-dimensional
 * vector).
 *
 * ## Examples
 *
 * ```kotlin
 * val locationArgument by location("location_arg")
 * ```
 */
@CommandDslMarker
public fun UsesCommandTree<CommandSourceStack>.location(
    name: String,
    suggestions: SuggestionsDsl<CommandSourceStack>? = null
): CommandArgument<CommandSourceStack, Location, Coordinates> = argument(
    name,
    Vec3Argument.vec3(),
    { label ->
        val vec3 = Vec3Argument.getVec3(this, label)
        Location(source.bukkitWorld, vec3.x, vec3.y, vec3.z)
    },
    suggestions
)