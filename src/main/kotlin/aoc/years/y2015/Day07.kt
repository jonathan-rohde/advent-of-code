package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import aoc.years.y2024.cache

fun main() {
    Day07().execute().printResults()
}

class Day07 : Day(
    year = 2015,
    day = 7,
    part1 = Part(test = null, testInput = "testInput"),
    part2 = Part(test = null, testInput = "testInput2"),
) {
    override fun part1(input: List<String>): Any {
        val rules = input.associate { it.parseLine() }
        return rules.followPath("a")
    }

    override fun part2(input: List<String>): Any {
        val rules = input.associate { it.parseLine() }
        val valueA = rules.followPath("a")

        val newRules = rules + ("b" to valueA.toString())
        return newRules.followPath("a")
    }
}

private fun String.parseLine(): Pair<String, String> {
    return split("->").let { it[1].trim() to it[0].trim() }
}

private fun Map<String, String>.followPath(start: String, pathCache: MutableMap<String, Int> = mutableMapOf()): Int {
    return pathCache.getOrPut(start) {
        val rule = this[start]!!
        return@getOrPut rule.digitOr {
            when {
                rule.matches("[a-z]+".toRegex()) -> followPath(rule, pathCache)
                rule.contains("NOT") -> rule.removePrefix("NOT ").digitOr { followPath(it, pathCache) }.inv()
                else -> {
                    val a = rule.takeWhile { it != ' ' }.digitOr { followPath(it, pathCache) }
                    val b = rule.takeLastWhile { it != ' ' }.digitOr { followPath(it, pathCache) }
                    when {
                        rule.contains("AND") -> a and b
                        rule.contains("OR") -> a or b
                        rule.contains("RSHIFT") -> a shr b
                        rule.contains("LSHIFT") -> a shl b
                        else -> throw IllegalArgumentException("Unknown rule: $rule")
                    }
                }
            }
        }
    }
}

private fun String.digitOr(action: (String) -> Int): Int {
    if (all { it.isDigit() }) {
        return toInt()
    }
    return action(this)
}