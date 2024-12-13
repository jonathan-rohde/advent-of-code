package aoc.years.y2024

import aoc.common.Day
import aoc.common.printResults

class Day03 : Day(2024, 3, 161 to 48) {
    override fun part1(input: List<String>): Int = input.joinToString(" ").calculate(true)

    override fun part2(input: List<String>): Int = input.joinToString(" ").calculate(false)
}

fun main() {
    Day03().execute().printResults()
}

private fun String.calculate(enabled: Boolean = true): Int {
    val pattern = """
        mul\((\d+),(\d+)\)
    """.trimIndent().toRegex()
    val matches = pattern.findAll(this)

    return matches.map {
        if (enabled) {
            return@map it.groupValues[1].toInt() * it.groupValues[2].toInt()
        }
        val prefix = this.substring(0, it.range.first)
        val enable = prefix.lastIndexOf("do()")
        val disable = prefix.lastIndexOf("don't()")
        if (disable == -1) {
            it.groupValues[1].toInt() * it.groupValues[2].toInt()
        } else {
            if (enable == -1 || enable < disable) {
                0
            } else {
                it.groupValues[1].toInt() * it.groupValues[2].toInt()
            }
        }
    }.sum()
}
