package aoc.years.y2025

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

private val testInput = """
    11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,2121212118-2121212124
""".trimIndent()

class Day02 : Day(
    year = 2025,
    day = 2,
    part1 = Part(test = 1227775554L, testInput = testInput),
    part2 = Part(test = 4174379265, testInput = testInput),
) {
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
