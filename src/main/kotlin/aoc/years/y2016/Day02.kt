package aoc.years.y2016

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

fun main() {
    Day02().execute().printResults()
}

val testInput = """
    ULL
    RRDDD
    LURDL
    UUUUD
""".trimIndent()

class Day02 : Day(
    year = 2016,
    day = 2,
    part1 = Part(test = "1985", testInput = testInput),
    part2 = Part(test = "5DB3", testInput = testInput),
) {
    override fun part1(input: List<String>) = execute(input, keypad)

    override fun part2(input: List<String>) = execute(input, keypadDiamond)

    private fun execute(input: List<String>, keypad: Map<Pair<String, Char>, String>): String {
        var start = "5"
        var result = ""
        input.forEach {
            start = it.toCharArray().pressKey(start, keypad)
            result += start
        }
        return result
    }
}

private fun CharArray.pressKey(start: String, keypad: Map<Pair<String, Char>, String>) = fold(start) { acc, ch ->
    keypad[Pair(acc, ch)] ?: acc
}

private val keypad = mapOf(
    Pair("1", 'R') to "2", Pair("1", 'D') to "4",
    Pair("2", 'L') to "1", Pair("2", 'D') to "5", Pair("2", 'R') to "3",
    Pair("3", 'L') to "2", Pair("3", 'D') to "6",
    Pair("4", 'U') to "1", Pair("4", 'R') to "5", Pair("4", 'D') to "7",
    Pair("5", 'U') to "2", Pair("5", 'L') to "4", Pair("5", 'R') to "6", Pair("5", 'D') to "8",
    Pair("6", 'L') to "5", Pair("6", 'U') to "3", Pair("6", 'D') to "9",
    Pair("7", 'R') to "8", Pair("7", 'U') to "4",
    Pair("8", 'L') to "7", Pair("8", 'R') to "9", Pair("8", 'U') to "5",
    Pair("9", 'L') to "8", Pair("9", 'U') to "6"
)

private val keypadDiamond = mapOf(
    Pair("1", 'D') to "3",
    Pair("2", 'R') to "3", Pair("2", 'D') to "6",
    Pair("3", 'L') to "2", Pair("3", 'R') to "4", Pair("3", 'D') to "7", Pair("3", 'U') to "1",
    Pair("4", 'L') to "3", Pair("4", 'D') to "8",
    Pair("5", 'R') to "6",
    Pair("6", 'U') to "2", Pair("6", 'R') to "7", Pair("6", 'D') to "A", Pair("6", 'L') to "5",
    Pair("7", 'L') to "6", Pair("7", 'R') to "8", Pair("7", 'U') to "3", Pair("7", 'D') to "B",
    Pair("8", 'U') to "4", Pair("8", 'L') to "7", Pair("8", 'R') to "9", Pair("8", 'D') to "C",
    Pair("9", 'L') to "8",
    Pair("A", 'U') to "6", Pair("A", 'R') to "B",
    Pair("B", 'U') to "7", Pair("B", 'L') to "A", Pair("B", 'D') to "D", Pair("B", 'R') to "C",
    Pair("C", 'U') to "8", Pair("C", 'L') to "B",
    Pair("D", 'U') to "B"
)
