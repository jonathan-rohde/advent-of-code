package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import utils.parseJson

fun main() {
    Day12().execute().printResults()
}

private val testInput = """
    [1,2,3]
    [[[3]]]
    {"a":2,"b":4}
    {"a":{"b":4},"c":-1}
    {"a":[-1,1]}
    [-1,{"a":1}]
    []
    {"abc": 4, "e": {"a": 4, "b": "red"}}
""".trimIndent()

class Day12 : Day(
    year = 2015,
    day = 12,
    part1 = Part(test = 26L, testInput = testInput),
    part2 = Part(test = 22L, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        val result = input.map {
            it.countNumbers()
        }
        return result.sum()
    }

    override fun part2(input: List<String>): Any {
        val result = input.map {
            it.countNumbers(excludeRed = true)
        }
        return result.sum()
    }
}

private fun String.countNumbers(excludeRed: Boolean = false): Long {
    return when(val result = parseJson()) {
        is ArrayList<*> -> result.countNumbers(excludeRed)
        is Map<*, *> -> result.countNumbers(excludeRed)
        else -> 0L
    }
}

private fun Map<*, *>.countNumbers(excludeRed: Boolean = false): Long {
    if (excludeRed && values.contains("red")) return 0L
    return entries.sumOf { (k, v) ->
        when (v) {
            is Number -> v.toLong()
            is String -> 0L
            is Map<*, *> -> v.countNumbers(excludeRed)
            is ArrayList<*> -> v.countNumbers(excludeRed)
            else -> throw IllegalArgumentException("unknown type: $v")
        }
    }
}

private fun ArrayList<*>.countNumbers(excludeRed: Boolean = false): Long {
    return sumOf { v ->
        when (v) {
            is Number -> v.toLong()
            is String -> 0L
            is Map<*, *> -> v.countNumbers(excludeRed)
            is ArrayList<*> -> v.countNumbers(excludeRed)
            else -> throw IllegalArgumentException("unknown type: ${v?.javaClass}")
        }
    }
}