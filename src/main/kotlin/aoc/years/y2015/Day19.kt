package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

fun main() {
    Day19().execute().printResults()
}

private val testInput = """
    H => HO
    H => OH
    O => HH
    e => HOHOHO
    
    HOHOHO
""".trimIndent()

class Day19 : Day(
    year = 2015,
    day = 19,
    part1 = Part(test = 7, testInput = testInput),
    part2 = Part(test = null, testInput = testInput),
) {

    override fun part1(input: List<String>): Any {
        val (replacement, text) = input.parseReplacements()
        return combinationsOf(text, replacement).size
    }
}

private fun List<String>.parseReplacements(): Pair<Map<String, List<String>>, String> {
    val replacements = takeWhile { it.isNotBlank() }
        .map { it.split(" => ") }
        .map { it[0] to it[1] }
        .groupBy { it.first }
        .mapValues { it.value.map { it.second } }
    val text = last()
    return Pair(replacements, text)
}

private fun combinationsOf(text: String, replacements: Map<String, List<String>>): Set<String> {
    val result = mutableSetOf<String>()
    replacements.entries.forEach { (key, values) ->
        var i = text.indexOf(key)
        while (i != -1) {
            result.addAll(text.replaceAll(i, i + key.length, values))
            i = text.indexOf(key, i + 1)
        }
    }
    return result
}

private fun String.replaceAll(from: Int, to: Int, replace: List<String>): Set<String> {
    val prefix = take(from)
    val suffix = drop(to)
    return replace.mapTo(mutableSetOf()) {
        "$prefix$it$suffix"
    }
}
