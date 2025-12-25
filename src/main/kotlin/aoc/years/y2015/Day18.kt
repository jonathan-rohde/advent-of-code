package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

fun main() {
    Day18().execute().printResults()
}

private val testInput = """
    .#.#.#
    ...##.
    #....#
    ..#...
    #.#..#
    ####..
""".trimIndent()

class Day18 : Day(
    year = 2015,
    day = 18,
    part1 = Part(test = 4, testInput = testInput, testLimit = 4, limit = 100),
    part2 = Part(test = 17, testInput = testInput, testLimit = 5, limit = 100),
) {

    override fun part1(input: List<String>, limit: Int): Any {
        var active: List<String> = ArrayList(input)

        repeat(limit) {
            active = active.switchLights()
        }

        return active.sumOf {
            it.count { c -> c == '#' }
        }
    }

    override fun part2(input: List<String>, limit: Int): Any {
        val fixedLights = listOf(
            0 to 0,
            0 to input.lastIndex,
            input[0].lastIndex to 0,
            input[0].lastIndex to input.lastIndex
        )
        var active: List<String> = input
            .mapIndexed { row, line ->
                line.mapIndexed { col, ch ->
                    if ((col to row) in fixedLights) {
                        '#'
                    } else {
                        ch
                    }
                }.joinToString("")
            }

        repeat(limit) {
            active = active.switchLights(fixedLights)
        }

        return active.sumOf {
            it.count { c -> c == '#' }
        }
    }
}

private fun List<String>.switchLights(fixedOn: List<Pair<Int, Int>> = emptyList()): List<String> {
    val result = mutableListOf<String>()
    forEachIndexed { row, line ->
        var newLine = ""
        line.forEachIndexed { col, place ->
            newLine += if (col to row in fixedOn) {
                '#'
            } else if (place == '#') {
                if (count8('#', col, row) in 2..3) {
                    '#'
                } else {
                    '.'
                }
            } else {
                if (count8('#', col, row) == 3) {
                    '#'
                } else {
                    '.'
                }
            }
        }
        result += newLine
    }
    return result
}

private fun List<String>.count8(c: Char, x: Int, y: Int): Int {
    return listOf(
        x - 1 to y - 1,
        x to y - 1,
        x + 1 to y - 1,
        x - 1 to y,
        x + 1 to y,
        x - 1 to y + 1,
        x to y + 1,
        x + 1 to y + 1
    ).filter { (x, y) -> y in indices && x in this[y].indices }
        .count { (x, y) -> this[y][x] == c }
}
