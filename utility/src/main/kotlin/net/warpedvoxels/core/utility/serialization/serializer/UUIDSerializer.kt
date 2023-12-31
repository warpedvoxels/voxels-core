package net.warpedvoxels.core.utility.serialization.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

public object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("java.util.UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: UUID): Unit = encoder.encodeString(value.toString())
}