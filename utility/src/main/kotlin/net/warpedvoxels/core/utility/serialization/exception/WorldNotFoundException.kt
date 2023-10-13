package net.warpedvoxels.core.utility.serialization.exception

/**
 * Thrown when a world is not found in the server's world list.
 * @param name The name of the world that was not found.
 */
public data class WorldNotFoundException(val name: String):
    Exception("Could not (de)serialize World(name=$name) as it was not found in the server's world list.")