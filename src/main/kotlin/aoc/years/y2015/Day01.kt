package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import kotlin.text.fold

fun main() {
    Day01().execute().printResults()
}

class Day01 : Day(
    year = 2015,
    day = 1,
    part1 = Part(test = -3, testInput = ")())())"),
    part2 = Part(test = -3, testInput = "()())"),
) {
    override fun part1(input: List<String>): Any {
        return input[0].determineFloor()
    }

    override fun part2(input: List<String>): Any {
        return with (input[0]) {
            indices.first {
                take(it + 1).determineFloor() == -1
            } + 1
        }
    }
}

private fun String.determineFloor(): Int {
    return fold(0) { acc, c ->
        if (c == '(') acc + 1 else acc - 1
    }
}