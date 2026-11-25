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
        return solve(input[0], Mode.SIMPLE)
    }

    override fun part2(input: List<String>): Any {
        return solve(input[0], Mode.EXTENDED)
    }

    private val encoderPattern = """\((\d+)x(\d+)\)(.*)""".toRegex()

    private fun solve(input: String, mode: Mode): Long {
        val plainChars = input.indexOf('(')
        if (plainChars > 0) return plainChars + solve(input.substring(plainChars), mode)
        if (plainChars == -1) return input.length.toLong()

        val (len, times, remaining) = encoderPattern.matchEntire(input)?.destructured?.let { (l, t, r) ->
            Triple(l.toInt(), t.toInt(), r)
        } ?: return 0L

        val sectionLength = when (mode) {
            Mode.SIMPLE -> len.toLong()
            Mode.EXTENDED -> solve(remaining.take(len), mode)
        }

        return times * sectionLength + solve(remaining.drop(len), mode)
    }

    private enum class Mode {
        SIMPLE, EXTENDED
    }
}
