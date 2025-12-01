package aoc.years.y2025

import aoc.common.Day
import aoc.common.printResults

class Day01 : Day(year = 2025, day = 1, test = 3 to 6) {
    override fun part1(input: List<String>): Int {
        return input.turnDials()
    }

    override fun part2(input: List<String>): Int {
        return input.turnDials2()
    }
}

fun main() {
    Day01().execute().printResults()
}


private fun List<String>.turnDials(): Int {
    var total = 0
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

private fun List<String>.turnDials2(): Int {
    var total = 0
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
