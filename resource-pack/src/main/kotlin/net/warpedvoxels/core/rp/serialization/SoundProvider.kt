package net.warpedvoxels.core.rp.serialization

import kotlinx.serialization.Serializable

/**
 * `sounds.json` is a file used by the sound system in resource packs which tells the sound system what sound files to
 * play when a sound event is triggered by one or more in-game events. This file is located in assets/minecraft in
 * resource packs.
 */
@Serializable
public data class SoundProvider(
    var replace: Boolean? = null,
    var subtitle: String? = null,
    var sounds: List<String>? = null
)

@Serializable
public data class SoundsEntrypoint(var sounds: Map<String, SoundProvider>)