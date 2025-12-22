package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

fun main() {
    Day08().execute().printResults()
}

private val testInput = """
    ""
    "abc"
    "aaa\"aaa"
    "\x27"
""".trimIndent()

class Day08 : Day(
    year = 2015,
    day = 8,
    part1 = Part(test = 12, testInput = testInput),
    part2 = Part(test = 19, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        val characters = input.sumOf { it.length }
        val inMemory = input.sumOf {
            it.memoryLength()
        }
        return characters - inMemory
    }
    override fun part2(input: List<String>): Any {
        val encoded = input.map { it.encode() }
        val characters = encoded.sumOf { it.length }
        val originalCharacters = input.sumOf { it.length }
        return characters - originalCharacters
    }
}

private fun String.memoryLength(): Int {
    var i = 0
    var length = 0
    while (i in indices) {
        if (this[i] != '\\') {
            length++
            i++
        } else {
            when (this[i + 1]) {
                'x' -> {
                    length++
                    i += 4
                }
                else -> {
                    length++
                    i += 2
                }
            }
        }
    }
    return length - 2
}

private val replacement = mapOf(
    '\\' to "\\\\",
    '"' to "\\\""
)
private fun String.encode(): String {
    var encoded = "\""
    for (c in this) {
        encoded += replacement.getOrDefault(c, c.toString())
    }

    return encoded + "\""
}
