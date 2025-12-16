package aoc.years.y2025

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import utils.toIntList

class Day12 : Day(
    year = 2025,
    day = 12,
    part1 = Part(testInput = TEST_INPUT_DAY_12),
    part2 = Part(testInput = TEST_INPUT_DAY_12)
) {
    override fun part1(input: List<String>) = input.parseGrids().countValidAreas()
}

fun main() {
    Day12().execute().printResults()
}

private val Pair<Int, Int>.area get() = this.first * this.second

private val List<Int>.shapeArea get() = sumOf { it * 9 }

private fun List<Pair<Pair<Int, Int>, List<Int>>>.countValidAreas() =
    count { (dim, shape) -> dim.area >= shape.shapeArea }.toLong()

private fun List<String>.parseGrids() = takeLastWhile { it.isNotBlank() }
    .map { line ->
        val (dimensions, shapes) = line.split(":")
        val (cols, rows) = dimensions.split("x").map { it.toInt() }
        Pair(cols, rows) to shapes.toIntList()
    }
