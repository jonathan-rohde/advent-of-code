package aoc.years.y2023

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import java.util.*

private val testInput = """
    R 6 (#70c710)
    D 5 (#0dc571)
    L 2 (#5713f0)
    D 2 (#d2c081)
    R 2 (#59c680)
    D 2 (#411b91)
    L 5 (#8ceee2)
    U 2 (#caa173)
    L 1 (#1b58a2)
    U 2 (#caa171)
    R 2 (#7807d2)
    U 3 (#a77fa3)
    L 2 (#015232)
    U 2 (#7a21e3)
""".trimIndent()

class Day18 : Day(
    year = 2023,
    day = 18,
    part1 = Part(test = 62, testInput = testInput),
    part2 = Part(test = 51, testInput = testInput),
) {

}

fun main() {
    Day18().execute().printResults()
}
