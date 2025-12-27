package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import kotlin.collections.shuffled

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

    override fun part2(input: List<String>): Any {
        val (replacement, text) = input.parseReplacements()
            .let { (r, t) ->
                r.flatMap { (key, values) -> values.map { key to it } } to t
            }

        return (1..25).minOf {
            executePart2(text, replacement)
        }
    }
}

private fun executePart2(text: String, replacement: List<Pair<String, String>>): Int {
    var current = text
    var steps = 0
    while (current != "e") {
        var hadChange = false
        replacement.shuffled().forEach { (key, value) ->
            if (current.contains(value)) {
                val index = current.lastIndexOf(value)
                current = current.replace(index, index + value.length, key)
                steps++
                hadChange = true
            }
        }
        if (!hadChange) {
            // start over :/
            current = text
        }
    }

    return steps
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

private fun String.replace(from: Int, to: Int, replace: String): String {
    val prefix = take(from)
    val suffix = drop(to)
    return "$prefix$replace$suffix"
}
