@file:JvmName("ResourcePackModel")

package net.warpedvoxels.core.rp.serialization.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.warpedvoxels.core.rp.dsl.ResourcePackDslMarker
import net.warpedvoxels.core.rp.math.Vector3f
import net.warpedvoxels.core.rp.math.Vector4f
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Holds the different places where item models are displayed.
 *
 * @param thirdPersonRightHand The position of the model when it's in the third person, right hand.
 * @param thirdPersonLeftHand The position of the model when it's in the third person, left hand.
 * @param firstPersonRightHand The position of the model when it's in the first person, right hand.
 * @param firstPersonLeftHand The position of the model when it's in the first person, left hand.
 * @param gui The position of the model when it's in the GUI.
 * @param head The position of the model when it's in the head.
 * @param ground The position of the model when it's in the ground.
 * @param fixed The position of the model when it's fixed.
 */
@Serializable
public data class ModelDisplay(
    @SerialName("thirdperson_righthand")
    var thirdPersonRightHand: ModelDisplayPosition? = null,
    @SerialName("thirdperson_lefthand")
    var thirdPersonLeftHand: ModelDisplayPosition? = null,
    @SerialName("firstperson_righthand")
    var firstPersonRightHand: ModelDisplayPosition? = null,
    @SerialName("firstperson_lefthand")
    var firstPersonLeftHand: ModelDisplayPosition? = null,
    var gui: ModelDisplayPosition? = null,
    var head: ModelDisplayPosition? = null,
    var ground: ModelDisplayPosition? = null,
    var fixed: ModelDisplayPosition? = null,
)

/**
 * Place where an item model is displayed. Holds its rotation, translation and scale for the specified situation. `fixed`
 * refers to item frames, while the rest are as their name states. Note that translations are applied to the model
 * before rotations.
 *
 * @param rotation    Specifies the rotation of the model according to the scheme [x, y, z].
 * @param translation Specifies the position of the model according to the scheme [x, y, z]. The values are clamped
 *                    between -80 and 80.
 * @param scale       Specifies the position of the model according to the scheme [x, y, z]. The values are clamped
 *                    between -80 and 80.
 */
@Serializable
public data class ModelDisplayPosition(
    var rotation: Vector3f = Vector3f.ZERO,
    var translation: Vector3f = Vector3f.ZERO,
    var scale: Vector3f = Vector3f.ONE,
) {
    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun rotation(block: Vector3f.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        rotation.apply(block)
    }

    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun translation(block: Vector3f.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        translation.apply(block)
    }

    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun scale(block: Vector3f.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        scale.apply(block)
    }
}

/**
 * Defines a texture variable and assigns a texture.
 */
@Serializable
public data class ModelTextures(
    var particle: String? = null,
    var all: String? = null,
    var top: String? = null,
    var bottom: String? = null,
    var north: String? = null,
    var south: String? = null,
    var east: String? = null,
    var west: String? = null,
    var layer0: String? = null,
    var layer1: String? = null,
    var layer2: String? = null,
    var layer3: String? = null,
    var layer4: String? = null,
    var layer5: String? = null,
    var layer6: String? = null,
    var layer7: String? = null,
    var layer8: String? = null,
    var layer9: String? = null,
    var layer10: String? = null,
    var layer11: String? = null,
    var layer12: String? = null,
    var layer13: String? = null,
    var layer14: String? = null,
    var layer15: String? = null,
) {
    public val allElements: List<String>
        get() = listOfNotNull(
            particle, all, top, bottom, north, south, east, west,
            layer0, layer1, layer2, layer3, layer4, layer5, layer6, layer7, layer8, layer9, layer10, layer11, layer12,
            layer13, layer14, layer15
        )
}


/**
 * Contains all the elements of the model. They can have only cubic forms. If both `"parent"` and `"elements"` are set,
 * the `"elements"` tag overrides the `"elements"` tag from the previous model.
 *
 * @param from      Start point of a cuboid according to the scheme [x, y, z]. Values must be between -16 and 32.
 * @param to        Stop point of a cuboid according to the scheme [x, y, z]. Values must be between -16 and 32.
 * @param rotation  Defines the rotation of an element.
 * @param shade     Defines if shadows are rendered (`true` - default), not (`false`).
 * @param faces     Holds all the faces of the cuboid. If a face is left out, it does not render.
 */
@Serializable
public data class ModelElement(
    var from: Vector3f,
    var to: Vector3f,
    var rotation: ModelElementRotation? = null,
    var shade: Boolean = true,
    var faces: ModelElementFaces,
) {
    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun from(block: Vector3f.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        from.apply(block)
    }

    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun to(block: Vector3f.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        to.apply(block)
    }

    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun rotation(block: ModelElementRotation.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        rotation = (rotation ?: ModelElementRotation(axis = 'x', angle = 0f)).apply(block)
    }

    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun faces(block: ModelElementFaces.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        faces.apply(block)
    }
}

/**
 * Defines the rotation of an element.
 *
 * @param origin  Sets the center of the rotation according to the scheme `[x, y, z]`. Defaults to `[8, 8, 8]`.
 * @param axis    Specifies the direction of rotation, can be `"x"`, `"y"` or `"z"`.
 * @param angle   Specifies the angle of rotation. Can be 45 through -45 degrees in 22.5 degree increments.
 * @param rescale Specifies whether to scale the faces across the whole block. Can be `true` or `false. Defaults to
 *                `false`.
 */
@Serializable
public data class ModelElementRotation(
    var origin: Vector3f = Vector3f(8f, 8f, 8f),
    var axis: Char,
    var angle: Float,
    var rescale: Boolean = false,
) {
    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun origin(block: Vector3f.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        origin.apply(block)
    }
}

/**
 * Holds all the faces of the cuboid. If a face is left out, it does not render.
 *
 * @param north The texture of the north face of the cuboid.
 * @param south The texture of the south face of the cuboid.
 * @param east The texture of the east face of the cuboid.
 * @param west The texture of the west face of the cuboid.
 * @param up The texture of the top face of the cuboid.
 * @param down The texture of the bottom face of the cuboid.
 */
@Serializable
public data class ModelElementFaces(
    var north: ModelElementFace? = null,
    var south: ModelElementFace? = null,
    var east: ModelElementFace? = null,
    var west: ModelElementFace? = null,
    var up: ModelElementFace? = null,
    var down: ModelElementFace? = null,
) {
    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun north(block: ModelElementFace.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        north = (north ?: ModelElementFace()).apply(block)
    }

    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun south(block: ModelElementFace.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        south = (south ?: ModelElementFace()).apply(block)
    }

    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun east(block: ModelElementFace.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        east = (east ?: ModelElementFace()).apply(block)
    }

    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun west(block: ModelElementFace.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        west = (west ?: ModelElementFace()).apply(block)
    }

    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun up(block: ModelElementFace.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        up = (up ?: ModelElementFace()).apply(block)
    }

    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun down(block: ModelElementFace.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        down = (down ?: ModelElementFace()).apply(block)
    }
}

/**
 * Defines a face of a cuboid.
 *
 * @param uv        Defines the area of the texture to use according to the scheme [x1, y1, x2, y2]. The texture
 *                  behavior is inconsistent if UV extends below 0 or above 16. If the numbers of x1 and x2 are swapped
 *                  (e.g., from 0, 0, 16, 16 to 16, 0, 0, 16), the texture flips. UV is optional, and if not supplied, it
 *                  automatically generates based on the element's position.
 * @param texture   Specifies the texture in form of the texture variable prepended with a #.
 * @param cullface  Specifies whether a face does not need to be rendered when there is a block touching it in the
 *                  specified position. The position can be: down, up, north, south, west, or east. It also determines
 *                  the side of the block to use the light level from for lighting the face, and if unset, defaults to
 *                  the side.
 * @param rotation  Rotates the texture by the specified number of degrees. Can be 0, 90, 180, or 270. Defaults to 0.
 *                  Rotation does not affect which part of the texture is used. Instead, it amounts to permutation of
 *                  the selected texture vertexes (selected implicitly, or explicitly though uv).
 * @param tintindex Determines whether to tint the texture using a hardcoded tint index. The default value, -1,
 *                  indicates not to use the tint. Any other number is provided to BlockColors to get the tint value
 *                  corresponding to that index. However, most blocks do not have a tint value defined
 *                  (in which case white is used). Furthermore, no vanilla block currently uses multiple tint values,
 *                  and thus the tint index value is ignored (as long as it is set to something other than -1);
 *                  it could be used for modded blocks that need multiple distinct tint values in the same block though.
 */
@Serializable
public data class ModelElementFace(
    var uv: Vector4f? = null,
    var texture: String? = null,
    var cullface: String? = null,
    var rotation: Int = 0,
    var tintindex: Int = -1,
) {
    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun uv(block: Vector4f.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        uv = (uv ?: Vector4f.ZERO).apply(block)
    }
}
