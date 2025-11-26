package aoc.years.y2025

import aoc.common.Day
import aoc.common.printResults

class Day12 : Day(year = 2025, day = 12, test = 11 to 31) {
    override fun part1(input: List<String>): Int {
        return 11
    }

    override fun part2(input: List<String>): Int {
        return 31
    }
}

fun main() {
    Day12().execute().printResults()
}