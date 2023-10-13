@file:JvmName("BukktSerializersModule")

package net.warpedvoxels.core.utility.serialization

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import net.warpedvoxels.core.utility.serialization.serializer.MaterialSerializer
import net.warpedvoxels.core.utility.serialization.serializer.UUIDSerializer
import net.warpedvoxels.core.utility.serialization.serializer.WorldSerializer

public val BukkitSerializersModule: SerializersModule get() = SerializersModule {
    contextual(WorldSerializer)
    contextual(MaterialSerializer)
    contextual(UUIDSerializer)
}