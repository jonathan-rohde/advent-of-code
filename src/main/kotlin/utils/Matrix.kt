package utils

import kotlin.math.roundToLong

data class Vector(val x: Double, val y: Double)

data class Matrix(val a: Double, val b: Double, val c: Double, val d: Double) {
    fun inverse(): Matrix {
        val det = a * d - b * c
        return Matrix(d / det, -b / det, -c / det, a / det)
    }

    fun times(v: Vector): Vector {
        return Vector(a * v.x + b * v.y, c * v.x + d * v.y)
    }
}

data class LGS(val matrix: Matrix, val vector: Vector) {
    private fun solve(): Vector = matrix.inverse().times(vector)

    fun wholeNumberSolution(): Vector? {
        val solution = solve()
        return if (isWholeNumberSolution(solution)) {
            solution
        } else {
            null
        }
    }

    internal fun isWholeNumberSolution(possibleSolution: Vector): Boolean {
        val roundedVector = Vector(
            possibleSolution.x.roundToLong().toDouble(),
            possibleSolution.y.roundToLong().toDouble()
        )
        return matrix.times(roundedVector) == vector
    }
}