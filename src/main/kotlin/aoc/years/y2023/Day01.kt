package aoc.years.y2023

import aoc.common.Day
import aoc.common.printResults

fun main() {
    Day01().execute().printResults()
}

class Day01 : Day(2023, 1 , 209 to 281) {
    override fun part1(input: List<String>): Int = input.sumOf { extractNumbers(it) }

    override fun part2(input: List<String>): Int = input.sumOf { extractNumberWords(it) }
}

fun extractNumbers(code: String): Int {
    val pattern = "[0-9]".toRegex()
    val matches = pattern.findAll(code)
    val numbers = matches.map { it.groupValues[0] }.toList()
    if (numbers.isEmpty()) {
        return 0
    }
    return codeNumber(numbers[0], numbers[numbers.size - 1])
}

val wordNumbers = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9
)

fun extractNumberWords(code: String): Int {
    val pattern = "(?=(one|two|three|four|five|six|seven|eight|nine|[0-9]))".toRegex()
    val matches = pattern.findAll(code)
    val numbers = matches.map { it.groupValues[1] }.toList()
    return codeNumber(numbers[0], numbers[numbers.size - 1])
}

fun codeNumber(first: String, second: String): Int {
    return "${codeNumber(first)}${codeNumber(second)}".toInt()
}

fun codeNumber(value: String): Int {
    if (Character.isDigit(value[0])) {
        return value.toInt()
    }
    return wordNumbers[value] ?: 0
}
