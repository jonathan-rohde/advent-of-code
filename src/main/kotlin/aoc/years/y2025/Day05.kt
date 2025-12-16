package aoc.years.y2025

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

private val testInput = """
    3-5
    10-14
    16-20
    12-18

    1
    5
    8
    11
    17
    32
""".trimIndent()

class Day05 : Day(
    year = 2025,
    day = 5,
    part1 = Part(test = 3L, testInput = testInput),
    part2 = Part(test = 14L, testInput = testInput),
) {
    override fun part1(input: List<String>): Long {
        val (ranges, numbers) = input.parseRangesAndNumbers()
        return numbers.count { num ->
            ranges.any { range -> num in range }
        }.toLong()
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
