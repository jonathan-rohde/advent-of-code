package aoc.years.y2016

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import java.security.MessageDigest

fun main() {
    Day06().execute().printResults()
}

private val testInput = """
    eedadn
    drvtee
    eandsr
    raavrd
    atevrs
    tsrnev
    sdttsa
    rasrtv
    nssdts
    ntnada
    svetve
    tesnvt
    vntsnd
    vrdear
    dvrsen
    enarar
""".trimIndent()


class Day06 : Day(
    year = 2016,
    day = 6,
    part1 = Part(test = "easter", testInput = testInput),
    part2 = Part(test = "advent", testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        return input.decodeMessage()
    }
    override fun part2(input: List<String>): Any {
        return input.decodeMessage(true)
    }
}

private fun List<String>.decodeMessage(min: Boolean = false): String {
    return this[0].indices
        .map { common(it, min) }
        .joinToString("")
}

private fun List<String>.common(col: Int, min: Boolean = false): Char {
    return map { it[col] }
        .groupBy { it }
        .mapValues { it.value.size }
        .entries
        .let {
            if (!min) {
                it.maxByOrNull { it.value }!!
            } else {
                it.minByOrNull { it.value }!!
            }
        }
        .key
}
