package aoc.years.y2024

import aoc.common.Day
import aoc.common.printResults
import utils.isSorted
import utils.toIntList
import kotlin.math.abs

class Day02 : Day(2024, 2, 2 to 4) {
    fun isSafe(input: List<Int>): Boolean {
        if (!input.isSorted()) return false
        val increment = input[0] < input[input.size - 1]
        val mistakes = input.zipWithNext().all {
            if (increment) {
                it.first < it.second && abs(it.first - it.second) <= 3
            } else {
                it.first > it.second && abs(it.first - it.second) <= 3
            }
        }
        return mistakes
    }

    fun isSafeTolerant(input: List<Int>): Boolean {
        if (isSafe(input)) {
            return true
        }
        input.forEachIndexed { index, _ ->
            val newLevels = input.toMutableList()
            newLevels.removeAt(index)
            if (isSafe(newLevels)) {
                return true
            }

        }
        return false
    }

    override fun part1(input: List<String>): Int {
        return input
            .filter { it.isNotEmpty() }
            .map { it.toIntList() }
            .count {
                isSafe(it)
            }
    }

    override fun part2(input: List<String>): Int {
        return input
            .filter { it.isNotEmpty() }
            .map { it.toIntList() }
            .count {
                isSafeTolerant(it)
            }
    }
}

fun main() {
    Day02().execute().printResults()
}
