package net.warpedvoxels.core.rp.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.key.Key

/**
 * The [Instrument] played depends on the material of the block underneath the note block.
 */
@Serializable(with = Instrument.Serializer::class)
public enum class Instrument(key: String) {
    Banjo("banjo"),
    BassDrum("basedrum"),
    Bass("bass"),
    Bell("bell"),
    Bit("bit"),
    Chime("chime"),
    CowBell("cow_bell"),
    Creeper("creeper"),
    CustomHead("custom_head"),
    Didgeridoo("didgeridoo"),
    Dragon("dragon"),
    Flute("flute"),
    Guitar("guitar"),
    Harp("harp"),
    Hat("hat"),
    IronXylophone("iron_xylophone"),
    Piglin("piglin"),
    Pling("pling"),
    Skeleton("skeleton"),
    Snare("snare"),
    WitherSkeleton("wither_skeleton"),
    Xylophone("xylophone"),
    Zombie("zombie");

    public val key: Key = Key.key(key)

    public object Serializer : KSerializer<Instrument> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
            "net.warpedvoxels.core.rp.serialization.Instrument", PrimitiveKind.STRING
        )

        override fun deserialize(decoder: Decoder): Instrument = Key.key(decoder.decodeString()).let {
            Instrument.entries.find { p -> p.key == it } ?: error("Cannot find instrument '$it'.")
        }

        override fun serialize(encoder: Encoder, value: Instrument): Unit = encoder.encodeString("${value.key}")
    }
}

//public fun Instrument.bukkit(): org.bukkit.Instrument = when (this) {
//    Instrument.Banjo -> org.bukkit.Instrument.BANJO
//    Instrument.Bass -> org.bukkit.Instrument.BASS_GUITAR
//    Instrument.BassDrum -> org.bukkit.Instrument.BASS_DRUM
//    Instrument.Bell -> org.bukkit.Instrument.BELL
//    Instrument.Bit -> org.bukkit.Instrument.BIT
//    Instrument.Chime -> org.bukkit.Instrument.CHIME
//    Instrument.CowBell -> org.bukkit.Instrument.COW_BELL
//    Instrument.Creeper -> org.bukkit.Instrument.CREEPER
//    Instrument.CustomHead -> org.bukkit.Instrument.CUSTOM_HEAD
//    Instrument.Didgeridoo -> org.bukkit.Instrument.DIDGERIDOO
//    Instrument.Dragon -> org.bukkit.Instrument.DRAGON
//    Instrument.Flute -> org.bukkit.Instrument.FLUTE
//    Instrument.Guitar -> org.bukkit.Instrument.GUITAR
//    Instrument.Harp -> org.bukkit.Instrument.PIANO
//    Instrument.IronXylophone -> org.bukkit.Instrument.IRON_XYLOPHONE
//    Instrument.Piglin -> org.bukkit.Instrument.PIGLIN
//    Instrument.Pling -> org.bukkit.Instrument.PLING
//    Instrument.Skeleton -> org.bukkit.Instrument.SKELETON
//    Instrument.Snare -> org.bukkit.Instrument.SNARE_DRUM
//    Instrument.WitherSkeleton -> org.bukkit.Instrument.WITHER_SKELETON
//    Instrument.Xylophone -> org.bukkit.Instrument.XYLOPHONE
//    Instrument.Hat -> org.bukkit.Instrument.STICKS
//    Instrument.Zombie -> org.bukkit.Instrument.ZOMBIE
//}