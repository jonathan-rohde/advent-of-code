package aoc.years.y2024

import aoc.common.Day
import aoc.common.printResults

class Day25 : Day(2024, 25, 3L to null) {
    override fun part1(input: List<String>): Long {
        val keyAndLocks = input.parse()
        val keys = keyAndLocks.filter { !it.second }.map { it.first }
        val locks = keyAndLocks.filter { it.second }.map { it.first }

        var result = 0L
        keys.forEach { key ->
            locks.forEach { lock ->
                if (keyMatchesLock(key, lock)) {
                    result++
                }
            }
        }

        return result
    }

    override fun part2(input: List<String>): String {
        return "Merry Christmas!"
    }
}

fun main() {
    Day25().execute().printResults()
}

private fun List<String>.parse(): List<Pair<List<Int>, Boolean>> {
    val result = mutableListOf<Pair<List<Int>, Boolean>>()

    for (i in indices step 8) {
        val lockKey = this.subList(i, i + 7).parseKey()
        val isLock = this[i] == "#####"

        result.add(lockKey to isLock)
    }


    return result
}

private fun List<String>.parseKey(): List<Int> {
    return this.map {
        val result = mutableListOf(0, 0, 0, 0, 0)
        for (i in it.indices) {
            if (it[i] == '#') {
                result[i] = 1
            }
        }
        result
    }
        .reduce { acc, ints ->
            val result = mutableListOf(0, 0, 0, 0, 0)
            for (i in acc.indices) {
                result[i] = acc[i] + ints[i]
            }
            result
        }
        .map { it - 1 }
}

private fun keyMatchesLock(key: List<Int>, lock: List<Int>): Boolean {
    key.forEachIndexed { index, i ->
        if (i + lock[index] > 5) {
            return false
        }
    }
    return true
}
