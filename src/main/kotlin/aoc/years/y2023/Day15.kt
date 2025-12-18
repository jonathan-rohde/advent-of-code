package aoc.years.y2023

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

private val testInput = """
    rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7
""".trimIndent()

class Day15 : Day(
    year = 2023,
    day = 15,
    part1 = Part(test = 1320, testInput = testInput),
    part2 = Part(test = 145, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        return input.parseSequence()
            .sumOf { it.hash() }
    }

    override fun part2(input: List<String>): Any {
        val hashMap = AoCHashMap()
        input.parseSequence()
            .forEach {
                if (it.endsWith("-")) {
                    hashMap.remove(it.removeSuffix("-"))
                } else if (it.contains("=")) {
                    val (key, value) = it.split("=")
                    hashMap.put(key, value.toInt())
                } else {
                    TODO()
                }
            }
        return hashMap.hash()
    }
}

fun main() {
    Day15().execute().printResults()
}

private class AoCHashMap {
    private val map = MutableList(256) {
        mutableListOf<Pair<String, Int>>()
    }

    fun put(key: String, value: Int) {
        val hash = key.hash()
        val box = map[hash]
        val existingIndex = box.indexOfFirst { it.first == key }
        if (existingIndex >= 0) {
            box[existingIndex] = key to value
        } else {
            box.add(key to value)
        }
    }

    fun remove(key: String) {
        val hash = key.hash()
        val box = map[hash]
        map[hash] = box.filter { it.first != key }.toMutableList()
    }

    fun hash(): Int {
        return map.mapIndexed { index, box ->
            (index + 1) * box.mapIndexed { slot, pair ->
                val (_, value) = pair
                (slot + 1) * value
            }.sum()
        }.sum()
    }
}

private fun String.hash(): Int {
    var hash = 0
    for (char in this) {
        hash += char.code
        hash *= 17
        hash %= 256
    }
    return hash
}

private fun List<String>.parseSequence(): List<String> =
    flatMap { line -> line.split(",").map { it.trim() }.filter { it.isNotBlank() } }
