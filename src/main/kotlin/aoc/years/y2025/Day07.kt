package aoc.years.y2025

import aoc.common.Day
import aoc.common.printResults
import utils.toLongList

class Day07 : Day(year = 2025, day = 7, test = 11 to 31) {
    override fun part1(input: List<String>): Int {
        return 11
    }

    override fun part2(input: List<String>): Int {
        return 31
    }
}

fun main() {
    Day07().execute().printResults()
}