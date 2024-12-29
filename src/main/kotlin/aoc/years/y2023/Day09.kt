package aoc.years.y2023

import aoc.common.Day
import aoc.common.printResults
import utils.toLongList

class Day09 : Day(year = 2023, day = 9, test = 114L to 2L) {
    override fun part1(input: List<String>): Any {
        return input.parse()
            .sumOf { it.completeSequence() }
    }

    override fun part2(input: List<String>): Any {
        return input.parse()
            .sumOf { it.completeBackwardsSequence() }
    }

}

fun main() {
    Day09().execute().printResults()
}

private fun List<String>.parse(): List<List<Long>> {
    return map { it.toLongList() }
}

private fun List<Long>.next(): List<Long> {
    return zipWithNext { a, b -> b - a }
}

private fun List<Long>.history(complete: () -> Long): Long {
    if (all { it == 0L }) {
        return 0L
    }
    return complete()
}

private fun List<Long>.completeSequence(): Long {
    return history { last() + next().completeSequence() }
}


private fun List<Long>.completeBackwardsSequence(): Long {
    return history { first() - next().completeBackwardsSequence() }
}
