package aoc.years.y2023

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import utils.toIntList
import kotlin.collections.mutableMapOf

private val testInput = """
    ???.### 1,1,3
    .??..??...?##. 1,1,3
    ?#?#?#?#?#?#?#? 1,3,1,6
    ????.#...#... 4,1,1
    ????.######..#####. 1,6,5
    ?###???????? 3,2,1
""".trimIndent()

class Day12 : Day(
    year = 2023,
    day = 12,
    part1 = Part(test = 21L, testInput = testInput),
    part2 = Part(test = 525152L, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        return input.sumOf { line ->
            line.parse().let { (placeholders, numbers) ->
                placeholders.findValidPattern(numbers)
            }
        }
    }

    override fun part2(input: List<String>): Any {
        return input.sumOf { line ->
            line.parse().let { (placeholders, numbers) ->
                (0..4).joinToString("?") { placeholders } to (0..4).flatMap { numbers}
            }.let { (placeholders, numbers) ->
                placeholders.findValidPattern(numbers)
            }
        }
    }
}

fun main() {
    Day12().execute().printResults()
}

private fun String.findValidPattern(
    numbers: List<Int>,
    cache: MutableMap<Pair<String, List<Int>>, Long> = mutableMapOf()
): Long {
    return cache.getOrPut(this to numbers) {

        if (none { it == '?' } && split(".").map { it.length }.filter { it > 0 } == numbers)
            return@getOrPut 1L

        if (this.startsWith(".")) {
            return@getOrPut this.drop(1).findValidPattern(numbers, cache)
        }
        if (this.startsWith("?")) {
            val resultWithDot = this.drop(1).findValidPattern(numbers, cache)
            val resultWithHash = "#${this.drop(1)}".findValidPattern(numbers, cache)
            return@getOrPut resultWithDot + resultWithHash
        }
        if (this.startsWith("#")) {
            if (numbers.isEmpty()) return@getOrPut 0L
            val required = numbers.first()
            val block = this.take(required)
            val next = this.drop(required).take(1)

            if (block.all { it == '#' }) {
                if (next == "." || next == "?") {
                    // found number
                    return@getOrPut this.drop(required + 1).findValidPattern(numbers.drop(1), cache)
                } else {
                    // next is a # and invalid
                    return@getOrPut 0L
                }
            } else if (block.contains("?")) {
                // still possible
                val replacement = replaceFirst('?', '#')
                return@getOrPut replacement.findValidPattern(numbers, cache)
            } else {
                // block contains . and thus invalid
                return@getOrPut 0L
            }
        }

        return@getOrPut 0L
    }
}

private fun String.parse(): Pair<String, List<Int>> {
    val pattern = takeWhile { it != ' ' }
    val numbers = dropWhile { it != ' ' }.toIntList()

    return Pair(pattern, numbers)
}
