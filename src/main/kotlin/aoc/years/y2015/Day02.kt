package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import kotlin.math.min
import kotlin.text.fold

fun main() {
    Day02().execute().printResults()
}

private val testInput = """
    2x3x4
    1x1x10
""".trimIndent()

class Day02 : Day(
    year = 2015,
    day = 2,
    part1 = Part(test = 101, testInput = testInput),
    part2 = Part(test = 48, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        return input.map { it.parseBox() }
            .sumOf { it.paper() + it.slack() }
    }

    override fun part2(input: List<String>): Any {
        return input.map { line -> line.parseBox()}
            .sumOf { it.ribbon() + it.bow() }
    }

}

private data class Box(val l: Int, val w: Int, val h: Int) {
    fun paper(): Int {
        return 2 * l * w + 2 * w * h + 2 * h * l
    }

    fun slack(): Int {
        return min(min(l*w, w*h), h*l)
    }

    fun bow(): Int {
        return l * w * h
    }

    fun ribbon(): Int {
        return listOf(l, w, h).sorted().take(2).sumOf { it + it }
    }
}

private fun String.parseBox(): Box {
    return split("x").let { Box(it[0].toInt(), it[1].toInt(), it[2].toInt()) }
}