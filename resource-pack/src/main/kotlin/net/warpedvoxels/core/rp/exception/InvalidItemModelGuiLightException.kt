package net.warpedvoxels.core.rp.exception

/**
 * Thrown when the library does not recognize a given item model `gui_light` value.
 * @param given The item model `gui_light` value that was not recognized.
 */
public data class InvalidItemModelGuiLightException(val given: String):
    Exception("Cannot (de)serialize ItemModelGuiLight($given) as it is invalid.")