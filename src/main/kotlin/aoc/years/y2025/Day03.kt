package aoc.years.y2025

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

private val testInput = """
    987654321111111
    811111111111119
    234234234234278
    818181911112111
""".trimIndent()
class Day03 : Day(
    year = 2025,
    day = 3,
    part1 = Part(test = 357L, testInput = testInput),
    part2 = Part(test = 3121910778619L, testInput = testInput)
) {
    override fun part1(input: List<String>): Long {
        return input.sumOf {bank ->
            bank.toBatteries().findMax(2).toLong()
        }
    }

    override fun part2(input: List<String>): Long {
        return input.sumOf { bank ->
            bank.toBatteries().findMax(12).toLong()
        }
    }
}

fun main() {
    Day03().execute().printResults()
}

private fun String.toBatteries(): List<Int> =
    map { it.digitToInt() }

private fun List<Int>.findMax(n: Int): String {
    if (n == 0) return ""

    val next = take(size - n + 1).max()
    val remaining = drop(indexOf(next) + 1).findMax(n - 1)
    return next.toString() + remaining
}
