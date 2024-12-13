package utils

import kotlin.math.roundToLong

data class Vector2(val x: Double, val y: Double)

data class Matrix2x2(val a: Double, val b: Double, val c: Double, val d: Double) {
    fun inverse(): Matrix2x2 {
        val det = a * d - b * c
        return Matrix2x2(
            a = d / det,
            b = -b / det,
            c = -c / det,
            d = a / det
        )
    }

    fun times(vector: Vector2): Vector2 {
        return Vector2(
            x = a * vector.x + b * vector.y,
            y = c * vector.x + d * vector.y)
    }
}

data class LGS2x2(val matrix: Matrix2x2, val vector: Vector2) {
    private fun solve(): Vector2 = matrix.inverse().times(vector)

    fun wholeNumberSolution(): Vector2? {
        val solution = solve()
        return when {
            isWholeNumberSolution(solution) -> solution
            else -> null
        }
    }

    private fun isWholeNumberSolution(possibleSolution: Vector2): Boolean {
        val roundedVector2 = Vector2(
            possibleSolution.x.roundToLong().toDouble(),
            possibleSolution.y.roundToLong().toDouble()
        )
        return matrix.times(roundedVector2) == vector
    }
}