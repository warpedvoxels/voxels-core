package net.warpedvoxels.core.rp.serialization

import kotlinx.serialization.Serializable

@Serializable
public data class ParticleProvider(var textures: Set<String>)