package de.jpx3.intave.check.physics2

import org.bukkit.util.Vector
import kotlin.math.absoluteValue

typealias BukkitVec = Vector

class Vector(x: Double, y: Double, z: Double) : BukkitVec(x, y, z) {
    constructor() : this(0.0, 0.0, 0.0)

    operator fun plus(summand: Double) = componentApply { it + summand }

    operator fun minus(subtrahend: Double) = componentApply { it - subtrahend }

    operator fun times(coefficient: Double) = componentApply { it * coefficient }

    operator fun div(coefficient: Double) = componentApply { it / coefficient }

    fun clamp(threshold: Double) = componentApply {
        return@componentApply if (it.absoluteValue < threshold) 0.0 else it
    }

    private inline fun componentApply(operation: (Double) -> Double): Vector {
        this.x = operation(x)
        this.y = operation(y)
        this.z = operation(z)
        return this
    }
}
