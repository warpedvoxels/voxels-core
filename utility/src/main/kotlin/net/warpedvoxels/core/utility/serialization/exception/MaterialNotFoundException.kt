package net.warpedvoxels.core.utility.serialization.exception


/**
 * Thrown when a material is not found in the game's material list.
 * @param name The name of the material that was not found.
 */
public data class MaterialNotFoundException(val name: String):
    Exception("Could not (de)serialize Material(name=$name) as it was not found in the game's material list.")