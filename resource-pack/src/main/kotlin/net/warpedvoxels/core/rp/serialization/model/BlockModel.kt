package net.warpedvoxels.core.rp.serialization.model

import kotlinx.serialization.Serializable
import net.warpedvoxels.core.rp.dsl.ResourcePackDslMarker
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * The folder `assets/<namespace>/models/block` holds the model files for all the specified variants. The names of the
 * files can be changed, but must always correspond with the names used in the valiant files.
 *
 * @param parent           The parent model this model will inherit from. All the elements from the parent model will be
 *                         inherited unless they are overridden in this model.
 * @param ambientocclusion Whether the model should be rendered with ambient occlusion (it only works on the parent
 *                         model).
 * @param display          Holds the different places where item models are displayed.
 * @param textures         Holds the textures of the model, in the form of a resource location or can be another texture
 *                         variable.
 * @param elements         Contains all the elements of the model. They can have only cubic forms. If both "parent" and
 *                         "elements" are set, the "elements" tag overrides the "elements" tag from the previous model.
 */
@Serializable
public data class BlockModel(
    var parent: String? = null,
    var ambientocclusion: Boolean? = null,
    var display: ModelDisplay? = null,
    var textures: ModelTextures? = null,
    var elements: List<ModelElement>? = null,
) {
    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun display(block: ModelDisplay.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        display = (display ?: ModelDisplay()).apply(block)
    }

    @OptIn(ExperimentalContracts::class)
    @ResourcePackDslMarker
    public fun textures(block: ModelTextures.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        textures = (textures ?: ModelTextures()).apply(block)
    }
}