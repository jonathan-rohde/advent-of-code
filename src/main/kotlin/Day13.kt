import utils.readInput
import utils.testAndPrint
import kotlin.math.abs
import kotlin.math.roundToLong

fun main() {
    fun part1(input: List<String>): Long {
        var result = 0L
        for (i in 0..input.lastIndex step 4) {
            val button1 = input[i].parseButton()
            val button2 = input[i + 1].parseButton()
            val price = input[i + 2].parsePrice()
            val matrix = Matrix(button1.x, button1.y, button2.x, button2.y)
            val solution = lgs(matrix, price)
            val sumPrice = solution.x.roundToLong() * 3 + solution.y.roundToLong()
            val solved = isSolved(matrix, solution, price)
            if (solved) {
                result += sumPrice
            }
        }
        return result
    }

    fun part2(input: List<String>): Long {
        var result = 0L
        for (i in 0..input.lastIndex step 4) {
            val button1 = input[i].parseButton()
            val button2 = input[i + 1].parseButton()
            val price = input[i + 2].parsePrice().let {
                Vector(it.x + 10000000000000, it.y + 10000000000000)
            }
            val matrix = Matrix(button1.x, button1.y, button2.x, button2.y)
            val solution = lgs(matrix, price)
            val sumPrice = solution.x.roundToLong() * 3 + solution.y.roundToLong()
            val solved = isSolved(matrix, solution, price)
            if (solved) {
                result += sumPrice
            }
        }
        return result
    }

    val testInput = readInput("Day13_test")
    part1(testInput).testAndPrint(480L)
    part2(testInput).testAndPrint()

    val input = readInput("Day13")
    part1(input).testAndPrint()
    part2(input).testAndPrint()
}

fun isSolved(matrix: Matrix, vector: Vector, expectation: Vector): Boolean {
    val roundedVector = Vector(
        vector.x.roundToLong().toDouble(),
        vector.y.roundToLong().toDouble()
    )
    return (
            matrix.a * roundedVector.x + matrix.c * roundedVector.y == expectation.x &&
                    matrix.b * roundedVector.x + matrix.d * roundedVector.y == expectation.y
            )
}

fun lgs(matrix: Matrix, vector: Vector): Vector {
    val solution = matrix.inverse().times(vector)
    return solution
}

fun String.parseButton(): Vector {
    val match = "X\\+([0-9]+), Y\\+([0-9]+)".toRegex().find(this) ?: error("Invalid button")
    val (x, y) = (match.groups[1]!!.value to match.groups[2]!!.value)
    return Vector(x.toDouble(), y.toDouble())
}

fun String.parsePrice(): Vector {
    val match = "X=([0-9]+), Y=([0-9]+)".toRegex().find(this) ?: error("Invalid button")
    val (x, y) = (match.groups[1]!!.value to match.groups[2]!!.value)
    return Vector(x.toDouble(), y.toDouble())
}

data class Vector(val x: Double, val y: Double)
data class Matrix(val a: Double, val b: Double, val c: Double, val d: Double) {
    fun inverse(): Matrix {
        val det = a * d - b * c
        return Matrix(d / det, -c / det, -b / det, a / det)
    }

    fun times(v: Vector): Vector {
        return Vector(a * v.x + b * v.y, c * v.x + d * v.y)
    }
}
