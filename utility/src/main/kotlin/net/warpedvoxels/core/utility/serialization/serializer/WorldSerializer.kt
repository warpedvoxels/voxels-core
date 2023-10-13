package net.warpedvoxels.core.utility.serialization.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.warpedvoxels.core.utility.serialization.exception.WorldNotFoundException
import org.bukkit.Bukkit
import org.bukkit.World

public object WorldSerializer : KSerializer<World> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("org.bukkit.World", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): World = decoder.decodeString().let {
        Bukkit.getWorld(it) ?: throw WorldNotFoundException(it)
    }

    override fun serialize(encoder: Encoder, value: World): Unit = encoder.encodeString(value.name)
}