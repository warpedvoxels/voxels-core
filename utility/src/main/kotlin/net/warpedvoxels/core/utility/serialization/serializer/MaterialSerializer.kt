package net.warpedvoxels.core.utility.serialization.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.warpedvoxels.core.utility.serialization.exception.MaterialNotFoundException
import org.bukkit.Material

public object MaterialSerializer : KSerializer<Material> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("org.bukkit.Material", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Material = decoder.decodeString().let {
        Material.entries.find { m -> m.name.equals(it, true) } ?: throw MaterialNotFoundException(it)
    }

    override fun serialize(encoder: Encoder, value: Material): Unit = encoder.encodeString(value.name)
}