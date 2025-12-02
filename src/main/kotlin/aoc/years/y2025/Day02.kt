package aoc.years.y2025

import aoc.common.Day
import aoc.common.printResults
import java.time.OffsetDateTime
import java.time.ZoneId

class Day02 : Day(year = 2025, day = 2, test = 1227775554L to 4174379265L) {
    override fun part1(input: List<String>): Long {
        return input[0].parseRanges()
            .sumOf {
                (it.first..it.second).filter { id -> !id.isValidId()  }.sum()
            }
    }

    override fun part2(input: List<String>): Long {
        return input[0].parseRanges()
            .sumOf {
                println("Processing range: ${it.first} to ${it.second}")
                (it.first..it.second).filter { id -> id.isPeriodicId()  }.sum()
            }
    }
}

fun main() {
    Day02().execute().printResults()
}


private fun String.parseRanges(): List<Pair<Long, Long>> {
    return split(",")
        .map { range ->
            val (start, end) = range.split("-").map { it.toLong() }
            start to end
        }
}

private val idCache = mutableMapOf<Long, Boolean>()

private fun Long.isValidId(): Boolean {
    if (idCache.containsKey(this)) {
        return idCache[this]!!
    }
    val str = toString()
    if (str.length % 2 != 0) return true
    val mid = str.length / 2
    val (first, second) = str.take(mid) to str.substring(str.length - mid)
    return (first != second).also { idCache[this] = it }
}

private val patternCache = mutableMapOf<Long, Boolean>()

private fun Long.isPeriodicId(): Boolean {
    if (patternCache.containsKey(this)) {
        return patternCache[this]!!
    }
    val str = toString()
    return (1..str.length / 2).any { size ->
        val pattern = str.take(size)
        val repeated = pattern.repeat(str.length / size)
        repeated == str
    }.also {
        patternCache[this] = it
    }
}
