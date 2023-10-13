package net.warpedvoxels.core.rp.serialization.model

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.warpedvoxels.core.rp.dsl.ResourcePackDslMarker
import net.warpedvoxels.core.rp.exception.InvalidItemModelGuiLightException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * As items do not have different variants, there is no need to specify them. The folder `assets/<namespace>/models/item`
 * contains all the model files. The names of the files are hardcoded and should not be changed.
 *
 * @param parent    The parent model this model will inherit from. All the elements from the parent model will be
 *                  inherited unless they are overridden in this model.
 * @param display   Holds the different places where item models are displayed.
 * @param textures  Holds the textures of the model, in the form of a resource location or can be another texture
 *                  variable.
 * @param elements  Contains all the elements of the model. They can have only cubic forms. If both "parent" and
 *                  "elements" are set, the "elements" tag overrides the "elements" tag from the previous model.
 * @param overrides Determines cases in which a different model should be used based on item tags. All cases are
 *                  evaluated in order from top to bottom and last predicate that matches overrides. However, overrides
 *                  are ignored if it has been already overridden once, for example, this avoids recursion on overriding
 *                  to the same model.
 */
@Serializable
public data class ItemModel(
    var parent: String? = null,
    var display: ModelDisplay? = null,
    var textures: ModelTextures? = null,
    var elements: MutableList<ModelElement>? = null,
    var overrides: MutableList<ItemModelOverride>? = null,
    var guiLight: ItemModelGuiLight? = null,
) {
    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun display(block: ModelDisplay.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        display = (display ?: ModelDisplay()).apply(block)
    }

    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun textures(block: ModelTextures.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        textures = (textures ?: ModelTextures()).apply(block)
    }

    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun override(block: ItemModelOverride.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        overrides = (overrides ?: mutableListOf()).apply {
            add(ItemModelOverride(model = "").apply(block))
        }
    }
}

/**
 * Determines cases in which a different model should be used based on item tags. All cases are evaluated in order from
 * top to bottom and last predicate that matches overrides. However, overrides are ignored if it has been already
 * overridden once, for example, this avoids recursion on overriding to the same model.
 *
 * @param predicate Holds the cases.
 * @param model     The path to the model to use if the case is met, in the form of a resource location.
 */
@Serializable
public data class ItemModelOverride(
    var predicate: ItemModelPredicateView = ItemModelPredicateView(),
    var model: String,
) {
    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun predicate(block: ItemModelPredicateView.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        predicate.apply(block)
    }
}

/**
 * Can be `"front"` or `"side"`. If set to `"side"`, the model is rendered like a block. If set to `"front"`, model is
 * shaded like a flat item. Defaults to `"side"`.
 */
@Serializable(with = ItemModelGuiLight.Serializer::class)
public enum class ItemModelGuiLight {
    Front, Side;

    public var value: String = name.lowercase()

    public companion object Serializer : KSerializer<ItemModelGuiLight> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("ItemModelGuiLight", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): ItemModelGuiLight = decoder.decodeString().let {
            ItemModelGuiLight.entries.find { i -> i.value == it } ?: throw InvalidItemModelGuiLightException(it)
        }

        override fun serialize(encoder: Encoder, value: ItemModelGuiLight): Unit = encoder.encodeString(value.value)
    }
}

/**
 * Some items support additional predicates for model overrides.
 */
@Serializable
public sealed interface ItemModelPredicate<T> {
    public abstract val value: T

    /** Used on compasses to determine the current angle, expressed in a decimal value of less than one. */
    @Serializable
    @JvmInline
    public value class Angle(override val value: Float) : ItemModelPredicate<Float>

    /** Used on shields to determine if currently blocking. If `1`, the player is blocking. */
    @Serializable
    @JvmInline
    public value class Blocking(override val value: Int) : ItemModelPredicate<Int>

    /** Used on Elytra to determine if broken. If `1`, the Elytra is broken. */
    @Serializable
    @JvmInline
    public value class Broken(override val value: Int) : ItemModelPredicate<Int>

    /** Used on fishing rods to determine if the fishing rod has been cast. If `1`, the fishing rod has been cast. */
    @Serializable
    @JvmInline
    public value class Cast(override val value: Int) : ItemModelPredicate<Int>

    /**
     * Used on ender pearls and chorus fruit to determine the remaining cooldown, expressed in a decimal value between
     * `0` and `1`.
     */
    @Serializable
    @JvmInline
    public value class Cooldown(override val value: Float) : ItemModelPredicate<Float>

    /**
     * Used on items with durability to determine the amount of damage, expressed in a decimal value between `0` and
     * `1`.
     */
    @Serializable
    @JvmInline
    public value class Damage(override val value: Float) : ItemModelPredicate<Float>

    /**
     * Used on items with durability to determine if it is damaged. If `1`, the item is damaged.
     * Note that if an item has the unbreakable tag, this may be `0` while the item has a non-zero "damage" tag.
     */
    @Serializable
    @JvmInline
    public value class Damaged(override val value: Int) : ItemModelPredicate<Int>

    /**
     * Determines the model used by left-handed players.
     * It affects the item they see in inventories, along with the item players see them holding or wearing.
     */
    @Serializable
    @JvmInline
    public value class LeftHanded(override val value: Int) : ItemModelPredicate<Int>

    /**
     * Determines the amount a bow or crossbow has been pulled, expressed in a decimal value of less than one.
     */
    @Serializable
    @JvmInline
    public value class Pull(override val value: Float) : ItemModelPredicate<Float>

    /**
     * Used on bows and crossbows to determine if the bow is being pulled. If `1`, the bow is currently being
     * pulled.
     */
    @Serializable
    @JvmInline
    public value class Pulling(override val value: Int) : ItemModelPredicate<Int>

    /**
     * Used on crossbows to determine if they are charged with any projectile. If `1`, the crossbow is charged.
     */
    @Serializable
    @JvmInline
    public value class Charged(override val value: Int) : ItemModelPredicate<Int>

    /**
     * Used on crossbows. If `1`, the crossbow is charged with a firework rocket.
     */
    @Serializable
    @JvmInline
    public value class Firework(override val value: Int) : ItemModelPredicate<Int>

    /**
     * Used on tridents to determine if the trident is ready to be thrown by the player. If `1`, the trident is ready
     * for fire.
     */
    @Serializable
    @JvmInline
    public value class Throwing(override val value: Int) : ItemModelPredicate<Int>

    /**
     * Used on clocks to determine the current time, expressed in a decimal value of less than one.
     */
    @Serializable
    @JvmInline
    public value class Time(override val value: Float) : ItemModelPredicate<Float>

    /**
     * Used on any item and is compared to the `tag.CustomModelData` NBT, expressed in an integer value. The number is
     * still internally converted to float, causing a precision loss for some numbers above 16 million.
     * If the value read from the item data is greater than or equal to the value used for the predicate, the predicate
     * is positive.
     */
    @Serializable
    @JvmInline
    public value class CustomModelData(override val value: Int) : ItemModelPredicate<Int>

    /**
     * Used on light blocks to determine the light level as contained in BlockStateTag,
     * expressed in a decimal value between `0` and `1`, where `1` is light level 15.
     */
    @Serializable
    @JvmInline
    public value class Level(override val value: Float) : ItemModelPredicate<Float>

    /**
     * Used on bundles to determine the ratio of the bundle's current contents to its total capacity,
     * expressed in a decimal value between `0` and `1`.
     */
    @Serializable
    @JvmInline
    public value class Filled(override val value: Float) : ItemModelPredicate<Float>

    /**
     * Used on goat horns to determine whether the player is tooting them. `1` for true, `0` for false.
     */
    @Serializable
    @JvmInline
    public value class Tooting(override val value: Int) : ItemModelPredicate<Int>

    /**
     * Used on armor to determine which material the applied trim is made of, expressed in a decimal value between `0`
     * and `1`.
     */
    @Serializable
    @JvmInline
    public value class TrimType(override val value: Int) : ItemModelPredicate<Int>

    /**
     * Used on brushes to determine the brushing animation progress,
     * expressed in a decimal value between `0` and `1`.
     */
    @Serializable
    @JvmInline
    public value class Brushing(override val value: Int) : ItemModelPredicate<Int>
}

/**
 * Some items support additional predicates for model overrides.
 */
@Serializable
public data class ItemModelPredicateView(
    var angle: ItemModelPredicate.Angle? = null,
    var blocking: ItemModelPredicate.Blocking? = null,
    var broken: ItemModelPredicate.Broken? = null,
    var cast: ItemModelPredicate.Cast? = null,
    var cooldown: ItemModelPredicate.Cooldown? = null,
    var damage: ItemModelPredicate.Damage? = null,
    var damaged: ItemModelPredicate.Damaged? = null,
    @SerialName("lefthanded")
    var leftHanded: ItemModelPredicate.LeftHanded? = null,
    var pull: ItemModelPredicate.Pull? = null,
    var pulling: ItemModelPredicate.Pulling? = null,
    var charged: ItemModelPredicate.Charged? = null,
    var firework: ItemModelPredicate.Firework? = null,
    var throwing: ItemModelPredicate.Throwing? = null,
    var time: ItemModelPredicate.Time? = null,
    @SerialName("custom_model_data")
    var customModelData: ItemModelPredicate.CustomModelData? = null,
    var level: ItemModelPredicate.Level? = null,
    var filled: ItemModelPredicate.Filled? = null,
    var tooting: ItemModelPredicate.Tooting? = null,
    @SerialName("trim_type")
    var trimType: ItemModelPredicate.TrimType? = null,
    var brushing: ItemModelPredicate.Brushing? = null
) {
    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun angle(block: ItemModelPredicate.Angle.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        angle = (angle ?: ItemModelPredicate.Angle(0f)).apply(block)
    }
}

