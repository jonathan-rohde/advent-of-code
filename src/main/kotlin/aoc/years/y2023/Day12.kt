package aoc.years.y2023

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import utils.toIntList

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
        return input.sumOf {
            val (placeholders, numbers) = it.parse()
            findValidPattern(placeholders, numbers)
        }
    }

}

fun main() {
    Day12().execute().printResults()
}

private fun findValidPattern(pattern: String, numbers: List<Int>): Long {
    if (Pair(pattern, numbers).isValid()) return 1L
    if (!pattern.contains('?')) return 0L

    val nextIndex = pattern.indexOf('?')
    val resultWithDot = findValidPattern(pattern.replaceRange(nextIndex, nextIndex + 1, "."), numbers)
    val resultWithHash = findValidPattern(pattern.replaceRange(nextIndex, nextIndex + 1, "#"), numbers)
    return resultWithDot + resultWithHash

}

private fun Pair<String, List<Int>>.isValid(): Boolean {
    val (pattern, numbers) = this
    if (pattern.any { it == '?' }) return false
    val chunks = pattern.split(".").map { it.length }.filter { it > 0 }
    return numbers == chunks
}

private fun String.parse(): Pair<String, List<Int>> {
    val pattern = takeWhile { it != ' ' }
    val numbers = dropWhile { it != ' ' }.toIntList()

    return Pair(pattern, numbers)
}
