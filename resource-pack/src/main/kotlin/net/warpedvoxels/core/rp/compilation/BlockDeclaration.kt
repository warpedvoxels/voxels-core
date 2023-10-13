package net.warpedvoxels.core.rp.compilation

import org.bukkit.NamespacedKey
import net.warpedvoxels.core.rp.serialization.model.*
import org.bukkit.Instrument
import org.bukkit.Material
import org.bukkit.Note

public data class BlockDeclaration(
    val identifier: NamespacedKey,
    val backendData: CustomBlockBackendData,
    val itemBaseMaterial: Material? = ItemDeclaration.DEFAULT_BASE_MATERIAL,
    val model: BlockModel,
    val index: UInt,
) {
    init {
        when (backendData) {
            is CustomBlockBackendData.NoteBlock -> require(index.toInt() <= CustomBlockBackendData.NoteBlock.LIMIT) {
                "Note block index must be less than or equal to ${CustomBlockBackendData.NoteBlock.LIMIT}."
            }
        }
    }

    public companion object {
        public const val BLOCK_INDEX_OFFSET: UInt = 100_000u
    }
}

@Suppress("UnstableApiUsage")
public sealed class CustomBlockBackendData(public val index: Int, public val baseBlockIdentifier: NamespacedKey) {
    public abstract fun buildBlockStateVariant(model: String): Pair<BlockStateVariantIdentifier, BlockStateModelVariant>

    public data class NoteBlock(
        val instrument: Instrument, val note: Note, val isPowered: Boolean
    ) : CustomBlockBackendData(
        instrument.type.toInt() * NOTE_BASE * POWERED_BASE + note.id.toInt() * POWERED_BASE + if (isPowered) 1 else 0,
        MODEL_LOCATION
    ) {
        public companion object {
            public const val NOTE_BASE: Int = 25
            public const val INSTRUMENT_BASE: Int = 23
            public const val POWERED_BASE: Int = 2
            public const val LIMIT: Int = 1149
            public val MODEL_LOCATION: NamespacedKey = NamespacedKey("minecraft", "note_block")
        }

        public constructor(index: Int) : this(
            Instrument.getByType((index / POWERED_BASE / NOTE_BASE % INSTRUMENT_BASE).toByte())
                ?: error("Invalid instrument type."),
            Note(index / POWERED_BASE % NOTE_BASE),
            index % POWERED_BASE == 1
        )

        override fun buildBlockStateVariant(model: String): Pair<BlockStateVariantIdentifier, BlockStateModelVariant> =
            BlockStateVariantIdentifier(
                "instrument" to "${instrument.namespacedKey}", "note" to "${note.id}", "powered" to "$isPowered"
            ) to BlockStateModelVariant(model = model)
    }
}