package de.jpx3.intave.check.physics2

import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.math.absoluteValue

typealias BukkitVec = Vector

fun Location.toVec(): Vec {
    return Vec(x, y, z)
}

class Vec(x: Double, y: Double, z: Double) : BukkitVec(x, y, z) {
    constructor() : this(0.0, 0.0, 0.0)

    private inline fun immutableComponentApply(operation: (Double) -> Double): Vec {
        return Vec(operation(x), operation(y), operation(z))
    }

    private inline fun immutableComponentApply(
        other: BukkitVec,
        operation: (a: Double, b: Double) -> Double
    ): Vec {
        return Vec(operation(x, other.x), operation(y, other.y), operation(z, other.z))
    }

    operator fun plus(summand: Double) = immutableComponentApply { it + summand }

    operator fun plus(other: Vector) = immutableComponentApply(other) { a, b -> a + b }

    operator fun minus(subtrahend: Double) = immutableComponentApply { it - subtrahend }

    operator fun minus(other: Vector) = immutableComponentApply(other) { a, b -> a - b }

    operator fun times(coefficient: Double) = immutableComponentApply { it * coefficient }

    operator fun times(other: Vector) = immutableComponentApply(other) { a, b -> a * b }

    operator fun div(coefficient: Double) = immutableComponentApply { it / coefficient }

    operator fun div(other: Vector) = immutableComponentApply(other) { a, b -> a / b }

    private inline fun mutableComponentApply(operation: (Double) -> Double) {
        this.x = operation(x)
        this.y = operation(y)
        this.z = operation(z)
    }

    private inline fun mutableComponentApply(
        other: BukkitVec,
        operation: (a: Double, b: Double) -> Double
    ) {
        this.x = operation(x, other.x)
        this.y = operation(y, other.y)
        this.z = operation(z, other.z)
    }

    operator fun plusAssign(summand: Double) = mutableComponentApply { it + summand }

    operator fun plusAssign(other: Vector) = mutableComponentApply(other) { a, b -> a + b }

    operator fun minusAssign(subtrahend: Double) = mutableComponentApply { it - subtrahend }

    operator fun minusAssign(other: Vector) = mutableComponentApply(other) { a, b -> a - b }

    operator fun timesAssign(coefficient: Double) = mutableComponentApply { it * coefficient }

    operator fun timesAssign(other: Vector) = mutableComponentApply(other) { a, b -> a * b }

    operator fun divAssign(coefficient: Double) = mutableComponentApply { it / coefficient }

    operator fun divAssign(other: Vector) = mutableComponentApply(other) { a, b -> a / b }

    fun clamp(threshold: Double) = immutableComponentApply {
        return@immutableComponentApply if (it.absoluteValue < threshold) 0.0 else it
    }

    fun set(x: Double, y: Double, z: Double) {
        this.x = x
        this.y = y
        this.z = z
    }

    fun set(other: BukkitVec) {
        set(other.x, other.y, other.z)
    }

    fun nullify() {
        set(0.0, 0.0, 0.0)
    }

    fun copy(): Vec {
        return Vec(x, y, z)
    }

    fun format(digits: Int = 3): String {
        return x.format(digits) + ", " + y.format(digits) + ", " + z.format(digits)
    }
}
