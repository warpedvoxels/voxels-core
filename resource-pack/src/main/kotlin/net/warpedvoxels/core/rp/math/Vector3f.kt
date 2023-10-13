package net.warpedvoxels.core.rp.math

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
public value class Vector3f(private val inner: Array<Float>) {
    public var x: Float
        get() = inner[0]
        set(value) {
            inner[0] = value
        }

    public var y: Float
        get() = inner[1]
        set(value) {
            inner[1] = value
        }


    public var z: Float
        get() = inner[2]
        set(value) {
            inner[2] = value
        }

    public operator fun plus(other: Vector3f): Vector3f = Vector3f(arrayOf(x + other.x, y + other.y, z + other.z))

    public operator fun minus(other: Vector3f): Vector3f = Vector3f(arrayOf(x - other.x, y - other.y, z - other.z))

    public operator fun times(other: Vector3f): Vector3f = Vector3f(arrayOf(x * other.x, y * other.y, z * other.z))

    public operator fun div(other: Vector3f): Vector3f = Vector3f(arrayOf(x / other.x, y / other.y, z / other.z))

    public operator fun rem(other: Vector3f): Vector3f = Vector3f(arrayOf(x % other.x, y % other.y, z % other.z))

    public operator fun plus(other: Float): Vector3f = Vector3f(arrayOf(x + other, y + other, z + other))

    public operator fun minus(other: Float): Vector3f = Vector3f(arrayOf(x - other, y - other, z - other))

    public operator fun times(other: Float): Vector3f = Vector3f(arrayOf(x * other, y * other, z * other))

    public operator fun div(other: Float): Vector3f = Vector3f(arrayOf(x / other, y / other, z / other))

    public operator fun rem(other: Float): Vector3f = Vector3f(arrayOf(x % other, y % other, z % other))

    public constructor(x: Float, y: Float, z: Float) : this(arrayOf(x, y, z))

    public companion object {
        public val ZERO: Vector3f = Vector3f(0f, 0f, 0f)
        public val ONE: Vector3f = Vector3f(1f, 1f, 1f)
    }
}