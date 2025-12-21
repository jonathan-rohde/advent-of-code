package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import kotlin.math.min
import kotlin.text.fold

fun main() {
    Day03().execute().printResults()
}

private val testInput = """
    ^v^v^v^v^v
""".trimIndent()

class Day03 : Day(
    year = 2015,
    day = 3,
    part1 = Part(test = 2, testInput = testInput),
    part2 = Part(test = 11, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        val houses = mutableSetOf(0 to 0)
        var current = 0 to 0
        input[0].forEach { c ->
            current = current.move(c)
            houses.add(current)
        }

        return houses.size
    }
    override fun part2(input: List<String>): Any {
        val houses = mutableSetOf(0 to 0)
        val robotHouses = mutableSetOf(0 to 0)
        var current = 0 to 0
        var currentRobot = 0 to 0
        input[0].windowed(2, step = 2).map { it[0] to it[1] }.forEach { (santa, robot) ->
            current = current.move(santa)
            houses.add(current)

            currentRobot = currentRobot.move(robot)
            robotHouses.add(currentRobot)
        }

        return (houses + robotHouses).size
    }
}

private fun Pair<Int, Int>.move(c: Char): Pair<Int, Int> {
    return when(c) {
        '>' -> first + 1 to second
        '<' -> first - 1 to second
        '^' -> first to second - 1
        'v' -> first to second + 1
        else -> this
    }
}