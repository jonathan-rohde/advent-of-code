package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

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
        this[start]!!.digitOr {
            when {
                this[start]!!.matches("[a-z]+".toRegex()) -> followPath(this[start]!!, pathCache)
                this[start]!!.contains("NOT") -> this[start]!!.removePrefix("NOT ")
                    .digitOr { followPath(it, pathCache) }.inv()
                else -> binaryOperation(start, pathCache)
            }
        }
    }
}

private fun Map<String, String>.binaryOperation(
    start: String,
    pathCache: MutableMap<String, Int>
): Int {
    val a = this[start]!!.takeWhile { it != ' ' }.digitOr { followPath(it, pathCache) }
    val b = this[start]!!.takeLastWhile { it != ' ' }.digitOr { followPath(it, pathCache) }
    return when {
        this[start]!!.contains("AND") -> a and b
        this[start]!!.contains("OR") -> a or b
        this[start]!!.contains("RSHIFT") -> a shr b
        this[start]!!.contains("LSHIFT") -> a shl b
        else -> throw IllegalArgumentException("Unknown rule: ${this[start]!!}")
    }
}

private fun String.digitOr(action: (String) -> Int): Int {
    if (all { it.isDigit() }) {
        return toInt()
    }
    return action(this)
}