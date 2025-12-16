package aoc.years.y2025

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

private val testInput = """
    L68
    L30
    R48
    L5
    R60
    L55
    L1
    L99
    R14
    L82
""".trimIndent()

class Day01 : Day(
    year = 2025,
    day = 1,
    part1 = Part(test = 3L, testInput = testInput),
    part2 = Part(test = 6L, testInput = testInput),
) {
    override fun part1(input: List<String>): Long {
        return input.turnDials()
    }

    override fun part2(input: List<String>): Long {
        return input.turnDials2()
    }
}

fun main() {
    Day01().execute().printResults()
}


private fun List<String>.turnDials(): Long {
    var total = 0L
    var current = 50
    for (line in this) {
        val clicks = line.substring(1).toInt() * line.directionToValue()
        current = (current + clicks + 100) % 100
        if (current == 0) {
            total += 1
        }
    }
    return total
}

private fun String.directionToValue(): Int = when (this[0]) {
    'L' -> -1
    'R' -> 1
    else -> throw IllegalArgumentException("Invalid direction")
}

private fun List<String>.turnDials2(): Long {
    var total = 0L
    var current = 50
    for (line in this) {
        val dist = line.substring(1).toInt()
        when (line[0]) {
            'L' -> {
                if (current == 0 && dist > 0) {
                    current = 100
                }
                repeat(dist) {
                    current -= 1
                    if (current < 0) {
                        current = 99
                    }
                    if (current == 0) {
                        total += 1
                    }
                }
            }
            'R' -> {
                repeat(dist) {
                    current += 1
                    if (current > 99) {
                        current = 0
                    }
                    if (current == 0) {
                        total += 1
                    }
                }
            }
        }
    }
    return total
}
