package net.warpedvoxels.core.rp.math

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
public value class Vector4f(private val inner: Array<Float>) {
    public val x: Float
        get() = inner[0]

    public val y: Float
        get() = inner[1]

    public val z: Float
        get() = inner[2]

    public val w: Float
        get() = inner[3]

    public operator fun plus(other: Vector4f): Vector4f = Vector4f(x + other.x, y + other.y, z + other.z, w + other.w)

    public operator fun minus(other: Vector4f): Vector4f = Vector4f(x - other.x, y - other.y, z - other.z, w - other.w)

    public operator fun times(other: Vector4f): Vector4f = Vector4f(x * other.x, y * other.y, z * other.z, w * other.w)

    public operator fun div(other: Vector4f): Vector4f = Vector4f(x / other.x, y / other.y, z / other.z, w / other.w)

    public operator fun rem(other: Vector4f): Vector4f = Vector4f(x % other.x, y % other.y, z % other.z, w % other.w)

    public operator fun plus(other: Float): Vector4f = Vector4f(x + other, y + other, z + other, w + other)

    public operator fun minus(other: Float): Vector4f = Vector4f(x - other, y - other, z - other, w - other)

    public operator fun times(other: Float): Vector4f = Vector4f(x * other, y * other, z * other, w * other)

    public operator fun div(other: Float): Vector4f = Vector4f(x / other, y / other, z / other, w / other)

    public operator fun rem(other: Float): Vector4f = Vector4f(x % other, y % other, z % other, w % other)

    public constructor(x: Float, y: Float, z: Float, w: Float) : this(arrayOf(x, y, z, w))

    public companion object {
        public val ZERO: Vector4f = Vector4f(0f, 0f, 0f, 0f)
        public val ONE: Vector4f = Vector4f(1f, 1f, 1f, 1f)
    }
}