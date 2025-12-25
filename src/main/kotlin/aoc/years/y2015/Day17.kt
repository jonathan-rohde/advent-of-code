package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

fun main() {
    Day17().execute().printResults()
}

private val testInput = """
    20
    15
    10
    5
    5
""".trimIndent()

class Day17 : Day(
    year = 2015,
    day = 17,
    part1 = Part(test = 4, testInput = testInput, testLimit = 25, limit = 150),
    part2 = Part(test = 3, testInput = testInput, testLimit = 25, limit = 150),
) {
    override fun part1(input: List<String>, limit: Int): Any {
        return input.toIntList().combinations(limit, counts = mutableMapOf())
    }
    override fun part2(input: List<String>, limit: Int): Any {
        val counts = mutableMapOf<Int, Int>()
        input.toIntList().combinations(
            liter = limit,
            counts = counts,
        )
        return counts.minBy { it.key }.value
    }
}

private fun List<String>.toIntList(): List<Int> = map { it.toInt() }

private fun List<Int>.combinations(liter: Int, used: List<Int> = emptyList(),
                                   counts: MutableMap<Int, Int>): Int {
    if (isEmpty() && liter == 0) {
        counts[used.size] = counts.getOrDefault(used.size, 0) + 1
        return 1
    }
    if (isEmpty()) return 0
    if (liter < 0) return 0

    val combinationsWithFirst = drop(1).combinations(liter - first(), used + first(), counts)
    val combinationsWithoutFirst = drop(1).combinations(liter, used, counts)

    return combinationsWithoutFirst + combinationsWithFirst

}
