package aoc.years.y2025

import aoc.common.Day
import aoc.common.printResults
import kotlin.math.abs

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
        when (line[0]) {
            'L' -> {
                current -= line.substring(1).toInt()
                current = (current + 100) % 100
            }
            'R' -> {
                current += line.substring(1).toInt()
                current %= 100
            }
        }
        if (current == 0) {
            total += 1
        }
    }
    return total
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
