package aoc.years.y2025

import aoc.common.Day
import aoc.common.printResults

class Day05 : Day(year = 2025, day = 5, test = 3 to 14L) {
    override fun part1(input: List<String>): Int {
        val (ranges, numbers) = input.parseRangesAndNumbers()
        return numbers.count { num ->
            ranges.any { range -> num in range }
        }
    }

    override fun part2(input: List<String>): Long {
        val (ranges, _) = input.parseRangesAndNumbers()
        return ranges.sumOf { it.size }
    }
}

fun main() {
    Day05().execute().printResults()
}

private val LongRange.size: Long
    get() = last - first + 1

private fun List<String>.parseRangesAndNumbers(): Pair<List<LongRange>, List<Long>> {
    val ranges = takeWhile { it.isNotBlank() }
        .map { it.split("-") }
        .map { (start, end) -> start.toLong()..end.toLong() }
        .mergeRanges()
    val numbers = dropWhile { it.isNotBlank() }.drop(1).map { it.toLong() }
    return ranges to numbers
}

private fun List<LongRange>.mergeRanges(): List<LongRange> {
    if (isEmpty()) return emptyList()

    val result = mutableListOf<LongRange>()
    with(sortedBy { it.first }) {
        var currentRange = first()
        drop(1).forEach { range ->
            if (range.isOverlapping(currentRange)) {
                currentRange += range
            } else {
                result.add(currentRange)
                currentRange = range
            }
        }
        result.add(currentRange)
    }
    return result
}

private fun LongRange.isOverlapping(currentRange: LongRange): Boolean = currentRange.last > first

private operator fun LongRange.plus(range: LongRange): LongRange = first..maxOf(last, range.last)
