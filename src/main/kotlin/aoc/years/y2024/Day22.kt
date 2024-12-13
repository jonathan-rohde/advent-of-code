package aoc.years.y2024

import aoc.common.Day
import aoc.common.printResults

class Day22 : Day(2024, 22, 37327623L to 24L) {
    override fun part1(input: List<String>): Long {
        return input.map { it.toLong() }.sumOf { it.calculateSecrets(2000)[2000] }
    }

    override fun part2(input: List<String>): Long {
        return input.map { it.toLong() }
            .map {
                it.calculateSecrets(2000)
            }
            .sellBananas()
    }
}

fun main() {
    Day22().execute().printResults()
}

private fun Long.calculateSecrets(count: Int): List<Long> {
    var current = this
    val result = mutableListOf(this)
    repeat(count) {
        val next = current * 64
        current = current.mix(next)
        current = current.prune()
        val next2 = current / 32
        current = current.mix(next2)
        current = current.prune()
        val next3 = current * 2048
        current = current.mix(next3)
        current = current.prune()
        result.add(current)
    }
    return result
}

private fun Long.mix(value: Long): Long {
    return value xor this
}

private fun Long.prune(): Long {
    return this % 16777216
}

private fun List<Long>.sequences(): List<Pair<List<Long>, Long>> {
    val result = mutableMapOf<List<Long>, Long>()
    map { it % 10 }
        .windowed(5) { (a, b, c, d, e) ->
            val key = listOf(b - a, c - b, d - c, e - d)
            if (!result.containsKey(key)) {
                result[key] = e
            }
    }
    return result.toList()
}

private fun List<List<Pair<List<Long>, Long>>>.groupBuyerSequences(): Map<List<Long>, List<Long>> {
    val result = mutableMapOf<List<Long>, MutableList<Long>>()

    forEach { buyer ->
        buyer
            .forEach { (key, value) ->
                result.getOrPut(key) { mutableListOf() }.add(value)
            }
    }


    return result
}

private fun List<List<Long>>.sellBananas(): Long {
    val sequences = map { it.sequences() }
    val sequenceGroups = sequences.groupBuyerSequences()
        .map { (key, value) -> key to value.sum() }
    val maxValue = sequenceGroups.maxBy { it.second }
    return maxValue.second
}
