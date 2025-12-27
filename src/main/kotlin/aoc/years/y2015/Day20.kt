package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

fun main() {
    Day20().execute().printResults()
}

private val testInput = """
    120
""".trimIndent()

class Day20 : Day(
    year = 2015,
    day = 20,
    part1 = Part(test = 6, testInput = testInput),
    part2 = Part(test = null, testInput = testInput),
) {

    override fun part1(input: List<String>): Any {
        val presents = input[0].toInt()
        val houses = generateHouses(presents / 10)
        houses.forEachIndexed { index, i ->
            if (i >= presents) return index
        }
        return -1
    }

    override fun part2(input: List<String>): Any {
        val presents = input[0].toInt()
        val houses = generateSecondHouses(presents / 10)
        houses.forEachIndexed { index, i ->
            if (i >= presents) return index
        }
        return -1
    }
}

private fun generateHouses(limit: Int): List<Int> {
    val result = MutableList(limit + 1) { 0 }
    (1..limit).forEach { i ->
        (i .. limit step i).forEach {index ->
            result[index] += i * 10
        }
    }
    return result
}


private fun generateSecondHouses(limit: Int): List<Int> {
    val result = MutableList(limit * 50 + 1) { 0 }
    (1..limit).forEach { i ->
        (1..50).forEach {index ->
            val house = index * i
            result[house] += i * 11
        }
    }
    return result
}
