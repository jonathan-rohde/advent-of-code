package aoc.years.y2025

import aoc.common.Day
import aoc.common.printResults

class Day03 : Day(year = 2025, day = 3, test = 357L to null) {
    override fun part1(input: List<String>): Long {
        return input.sumOf {bank ->
            bank.toBatteries().findMax(2).toLong()
        }
    }

    override fun part2(input: List<String>): Long {
        return input.sumOf { bank ->
            bank.toBatteries().findMax(12).toLong()
        }
    }
}

fun main() {
    Day03().execute().printResults()
}

private fun String.toBatteries(): List<Int> =
    map { it.digitToInt() }

private fun List<Int>.findMax(n: Int): String {
    if (n == 0) return ""

    val next = take(size - n + 1).max()
    val remaining = drop(indexOf(next) + 1).findMax(n - 1)
    return next.toString() + remaining
}
