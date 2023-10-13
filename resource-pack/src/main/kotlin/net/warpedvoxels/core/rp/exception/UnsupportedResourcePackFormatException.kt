package net.warpedvoxels.core.rp.exception

/**
 * Thrown when the library does not support a resource pack format.
 * @param format The format that was not supported.
 */
public data class UnsupportedResourcePackFormatException(val format: Int):
    Exception("Cannot (de)serialize ResourcePackFormat(value=$format) as it is not supported by this version of the library.")