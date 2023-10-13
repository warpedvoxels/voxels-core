package net.warpedvoxels.core.rp.serialization.meta

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.warpedvoxels.core.rp.exception.UnsupportedResourcePackFormatException

/**
 * This number is used to declare the format of a resource pack in `pack.mcmeta`.
 */
@Serializable(with = ResourcePackFormat.Serializer::class)
@SerialName("pack_format")
public enum class ResourcePackFormat(public val value: Int) {
    v1_19_4(13),
    v1_20_x(15);
    
    public object Serializer : KSerializer<ResourcePackFormat> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("ResourcePackFormat", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ResourcePackFormat = decoder.decodeInt().let {
            ResourcePackFormat.entries.find { r -> r.value == it } ?: throw UnsupportedResourcePackFormatException(it)
        }

        override fun serialize(encoder: Encoder, value: ResourcePackFormat): Unit = encoder.encodeInt(value.value)
    }
}