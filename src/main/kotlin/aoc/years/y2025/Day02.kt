package aoc.years.y2025

import aoc.common.Day
import aoc.common.printResults

class Day02 : Day(year = 2025, day = 2, test = 1227775554L to 4174379265L) {
    override fun part1(input: List<String>): Long {
        return input[0].parseRanges().sumOf {
            (it.first..it.second).filter { id -> !id.isValidId() }.sum()
        }
    }

    override fun part2(input: List<String>): Long {
        return input[0].parseRanges().sumOf {
            (it.first..it.second).filter { id -> id.isPeriodicId() }.sum()
        }
    }
}

fun main() {
    Day02().execute().printResults()
}


private fun String.parseRanges(): List<Pair<Long, Long>> {
    return split(",").map { range ->
        val (start, end) = range.split("-").map { it.toLong() }
        start to end
    }
}

private val idCache = mutableMapOf<Long, Boolean>()

private fun Long.isValidId() = idCache.getOrPut(this) {
    with(toString()) {
        if (length % 2 != 0) return true
        val (first, second) = take(length / 2) to takeLast(length / 2) // to str.substring(str.length - mid)
        (first != second)
    }
}

private val patternCache = mutableMapOf<Long, Boolean>()

private fun Long.isPeriodicId() = patternCache.getOrPut((this)) {
    with(toString()) {
        (1..length / 2).any { size ->
            val repeated = this.take(size).repeat(length / size)
            repeated == this
        }
    }
}
