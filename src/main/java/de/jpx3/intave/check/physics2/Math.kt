package de.jpx3.intave.check.physics2

const val DEGREE_TO_RAD = (Math.PI / 180.0).toFloat()
fun Double.format(digits: Int = 3): String {
    return String.format("%.${digits}f", this)
}
