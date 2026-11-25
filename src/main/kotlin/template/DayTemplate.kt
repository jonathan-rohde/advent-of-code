package template

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

fun main() {
    DayXX().execute().printResults()
}

private val testInput = """
""".trimIndent()

class DayXX : Day(
    year = 0,
    day = 0,
    part1 = Part(test = null, testInput = testInput),
    part2 = Part(test = null, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        return -1L
    }

    override fun part2(input: List<String>): Any {
        return -1L
    }
}
