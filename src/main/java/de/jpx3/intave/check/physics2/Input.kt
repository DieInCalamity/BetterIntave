package de.jpx3.intave.check.physics2

import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

object Input {
    val inputs = Array(9) {
        val angle = it * 45.0 * DEGREE_TO_RAD

        val forward = sin(angle).roundToInt()
        val strafe = cos(angle).roundToInt()

        Vec(forward.toDouble(), 0.0, strafe.toDouble())
    }
}
