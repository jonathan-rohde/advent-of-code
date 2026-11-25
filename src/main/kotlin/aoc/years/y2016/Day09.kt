package aoc.years.y2016

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

fun main() {
    Day09().execute().printResults()
}

private val testInput = """
    X(8x2)(3x3)ABCY
""".trimIndent()

class Day09 : Day(
    year = 2016,
    day = 9,
    part1 = Part(test = 18L, testInput = testInput),
    part2 = Part(test = 20L, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        return solve(input[0], MODE.SIMPLE)
    }

    override fun part2(input: List<String>): Any {
        return solve(input[0], MODE.EXTENDED)
    }

    private fun solve(input: String, mode: MODE): Long {
        if (input.isEmpty()) return 0L
        if (input[0] != '(') return 1L + solve(input.substring(1), mode)

        val (len, times, remaining) = ("\\(([0-9]+)x([0-9]+)\\)(.*)".toRegex().matchEntire(input)?.destructured )?.let {
            (l, t, r) -> Triple(l.toInt(), t.toInt(), r)
        } ?: return 0L

        return when (mode) {
            MODE.SIMPLE -> len * times + solve(remaining.drop(len), mode)
            MODE.EXTENDED -> {
                val recursionString = remaining.take(len)
                if (!recursionString.contains("(")) return len * times + solve(remaining.drop(len), mode)
                val subSolve = solve(recursionString, mode)

                times * subSolve + solve(remaining.drop(len), mode)
            }
        }
    }

    private enum class MODE {
        SIMPLE, EXTENDED
    }
}
