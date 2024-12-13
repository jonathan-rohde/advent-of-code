package aoc.years.y2024

import aoc.common.Day
import aoc.common.printResults
import utils.LGS2x2
import utils.Matrix2x2
import utils.Vector2
import kotlin.math.roundToLong

class Day13 : Day(2024, 13, 480L to 875318608908L) {
    override fun part1(input: List<String>): Long = puzzle(input = input)

    override fun part2(input: List<String>): Long = puzzle(input = input, offset = 10000000000000)


}

fun main() {
    Day13().execute().printResults()
}

fun puzzle(input: List<String>, offset: Long = 0): Long =
    (0..input.lastIndex step 4)
        .map { i ->
            Triple(
                first = input[i].parseButton(),
                second = input[i + 1].parseButton(),
                third = input[i + 2].parsePrice().let { vec ->
                    Vector2(vec.x + offset, vec.y + offset)
                })
        }
        .sumOf { (button1, button2, price) ->
            val solution = LGS2x2(
                matrix = Matrix2x2(button1.x, button2.x, button1.y, button2.y),
                vector = price
            )
                .wholeNumberSolution() ?: Vector2(0.0, 0.0)
            solution.x.roundToLong() * 3 + solution.y.roundToLong()
        }

fun String.parseButton(): Vector2 {
    val match = "X\\+([0-9]+), Y\\+([0-9]+)".toRegex().find(this) ?: error("Invalid button")
    val (x, y) = (match.groups[1]!!.value to match.groups[2]!!.value)
    return Vector2(x.toDouble(), y.toDouble())
}

fun String.parsePrice(): Vector2 {
    val match = "X=([0-9]+), Y=([0-9]+)".toRegex().find(this) ?: error("Invalid button")
    val (x, y) = (match.groups[1]!!.value to match.groups[2]!!.value)
    return Vector2(x.toDouble(), y.toDouble())
}
