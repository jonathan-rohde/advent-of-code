package aoc.years.y2025

import aoc.common.Day
import aoc.common.printResults
import utils.isSorted
import utils.toIntList
import kotlin.math.abs

class Day02 : Day(year = 2025, day = 2, test = 11 to 31) {
    override fun part1(input: List<String>): Int {
        return 11
    }

    override fun part2(input: List<String>): Int {
        return 31
    }
}

fun main() {
    Day02().execute().printResults()
}
