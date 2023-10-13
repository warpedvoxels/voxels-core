package net.warpedvoxels.core.rp.serialization.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.warpedvoxels.core.rp.dsl.ResourcePackDslMarker
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Blocks may have different models depending on their variants, hence the need for a model definition that
 * lists all its existing variants and links them to their respective models. Blocks can also be composed of several
 * different models at the same time, called "multipart". The models are then used depending on the states of
 * the block.
 *
 * These files are stored in the following folder: `assets/<namespace>/blockstates`. The files are used directly based
 * on their filename, thus a block state file with another name than the existing ones does not affect any block.
 *
 * @param variants  A map of all the variants of the block. The key consists of the relevant block states separated by
 *                  commas. A block with just one variant uses `""` as a name for its variant. Each variant can have
 *                  one model or an array of models and contain their properties.
 * @param multipart Used instead of variants to combine models based on block state attributes.
 */
@Serializable
public data class BlockStateModel(
    var variants: Map<BlockStateVariantIdentifier, BlockStateModelVariant>? = null,
    var multipart: List<BlockStateModelMultipart>? = null,
)

/**
 * A variant of a block state model. A block can have several variants, each with its own model.
 *
 * @param model  Specifies the path to the model file of the block, in the form of a resource location.
 * @param x      Rotation of the model on the x-axis in increments of 90 degrees.
 * @param y      Rotation of the model on the y-axis in increments of 90 degrees.
 * @param uvlock Locks the rotation of the texture of a block, if set to `true`. This way the texture does not rotate
 *               with the block when using the x and y-tags above.
 * @param weight Sets the probability of the model for being used in the game, defaults to 1 (=100%). If more than one
 *               model is used for the same variant, the probability is calculated by dividing the individual model's
 *               weight by the sum of the weights of all models.
 */
@Serializable
public data class BlockStateModelVariant(
    var model: String,
    var x: Int? = null,
    var y: Int? = null,
    var uvlock: Boolean? = null,
    var weight: Int? = null,
)

/**
 * A multipart of a block state model. A block can have several multipart, each with its own model. Used instead of
 * variants to combine models based on block state attributes.
 *
 * @param when  A list of cases that have to be met for the model to be applied. If unset, the model always applies.
 * @param apply The model to apply if the cases are met.
 */
@Serializable
public data class BlockStateModelMultipart(
    var `when`: BlockStateModelMultipartCase = BlockStateModelMultipartCase.Default(emptyMap()),
    var apply: BlockStateModelVariant = BlockStateModelVariant(""),
) {
    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun apply(block: BlockStateModelVariant.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        apply.apply(block)
    }
}

/**
 * A list of cases that have to be met for the model to be applied. If unset, the model always applies.
 */
@Serializable
public sealed interface BlockStateModelMultipartCase {
    /**
     * Name of a block state. A single case that has to match one of the block states. It can be set to a list
     * separated by | to allow multiple values to match. Cannot be set alongside the OR-tag or AND-tag.
     */
    @JvmInline
    @Serializable
    public value class Default(public val value: Map<String, String>) : BlockStateModelMultipartCase

    /**
     * Matches if any of the contained cases return true. Cannot be set alongside other cases.
     */
    @JvmInline
    @Serializable
    public value class Or(public val value: List<Default>) : BlockStateModelMultipartCase

    /**
     * Matches if all the contained cases return true. Cannot be set alongside other cases.
     */
    @JvmInline
    @Serializable
    public value class And(public val value: List<Default>) : BlockStateModelMultipartCase

    public companion object {
        public operator fun invoke(value: Map<String, String>): BlockStateModelMultipartCase = Default(value)
    }
}

@Serializable(with = BlockStateVariantIdentifier.Serializer::class)
public data class BlockStateVariantIdentifier(val value: Map<String, String>) {
    override fun toString(): String = value.entries.joinToString(",") { (k, v) -> "$k=$v" }

    public companion object Serializer: KSerializer<BlockStateVariantIdentifier> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("BlockStateVariantIdentifier", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): BlockStateVariantIdentifier =
            BlockStateVariantIdentifier(decoder.decodeString())

        override fun serialize(encoder: Encoder, value: BlockStateVariantIdentifier): Unit =
           encoder.encodeString(value.toString())
    }

    public constructor(vararg pairs: Pair<String, String>): this(pairs.toMap())

    public constructor(input: String): this(
        input.split(",").map { s -> s.split("=") }.associate { (k, v) -> k to v }
    )
}