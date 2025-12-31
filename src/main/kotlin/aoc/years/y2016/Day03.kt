package aoc.years.y2016

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import utils.toIntList

fun main() {
    Day03().execute().printResults()
}

private val testInput = """
    5 10 25
    10 10 10
""".trimIndent()

private val testInput2 = """
    101 301 501
    102 302 502
    103 303 503
    201 401 601
    202 402 602
    203 403 603
""".trimIndent()

class Day03 : Day(
    year = 2016,
    day = 3,
    part1 = Part(test = 1, testInput = testInput),
    part2 = Part(test = 6, testInput = testInput2),
) {
    override fun part1(input: List<String>): Any {
        return input.map { it.parseTriangle() }
            .count { it.isValidTriangle() }
    }
    override fun part2(input: List<String>): Any {
        return input.map { it.toIntList() }
            .toRotateTriangles()
            .count { it.isValidTriangle() }
    }
}

private fun String.parseTriangle(): Triple<Int, Int, Int> {
    return toIntList().let {
        Triple(it[0], it[1], it[2])
    }
}

private fun Triple<Int, Int, Int>.isValidTriangle(): Boolean {
    return first + second > third && first + third > second
            && second + third > first
}

private fun List<List<Int>>.toRotateTriangles(): List<Triple<Int, Int, Int>> {
    val result = mutableListOf<Triple<Int, Int, Int>>()
    (0..this[0].lastIndex).forEach { col ->
        (0 .. lastIndex step 3).forEach { row ->
            result.add(Triple(this[row][col],this[row + 1][col],this[row + 2][col]))
        }
    }
    return result
}
