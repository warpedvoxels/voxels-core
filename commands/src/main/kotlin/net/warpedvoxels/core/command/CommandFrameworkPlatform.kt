package net.warpedvoxels.core.command

/**
 * Platform-specific functionality.
 */
public interface CommandFrameworkPlatform<S> {
    /**
     * @return Whether [sender] has permission of [permission].
     */
    public fun hasPermission(sender: S, permission: String): Boolean
}