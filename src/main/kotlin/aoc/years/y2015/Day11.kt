package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

fun main() {
    Day11().execute().printResults()
}

private val testInput = """
    abcdefgh
""".trimIndent()

class Day11 : Day(
    year = 2015,
    day = 11,
    part1 = Part(test = "abcdffaa", testInput = testInput),
    part2 = Part(test = "abcdffbb", testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        return input.joinToString { it.nextPassword() }
    }

    override fun part2(input: List<String>): Any {
        return input.joinToString { it.nextPassword().inc().nextPassword() }
    }
}

private fun String.inc(): String {
    val shift = last() == 'z'
    val last = this.last().let {
        if (shift) 'a' else it.inc()
    }
    if (!shift) {
        return dropLast(1) + last
    }
    return dropLast(1).inc() + last
}

private fun String.validPassword(): Boolean {
    if (any { it == 'i' || it == 'o' || it == 'l' }) return false
    if (windowed(3).map { Triple(it[0].code, it[1].code, it[2].code)}
        .none { (a, b, c) ->
            c - b == 1 && b - a == 1
        }) return false
    if (!overlappingPairs()) return false

    return true
}

private fun String.overlappingPairs(): Boolean {
    val pairs = windowed(2).asSequence().map { Pair(it[0].code, it[1].code) }.distinct()
        .filter { (a, b) -> a == b }
        .toList()
    return pairs.size >= 2
}

private fun String.nextPassword(): String {
    var current = this
    while (!current.validPassword()) {
        current = current.inc()
    }
    return current
}
