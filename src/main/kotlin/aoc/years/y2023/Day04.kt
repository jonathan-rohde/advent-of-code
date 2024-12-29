package aoc.years.y2023

import aoc.common.Day
import aoc.common.printResults
import utils.toIntList

class Day04 : Day(year = 2023, day = 4, test = 13 to 30) {
    override fun part1(input: List<String>): Any {
        return input.parseCards()
            .sumOf { it.getValue().first }
    }

    override fun part2(input: List<String>): Any {
        val cards = input.parseCards()
        val interim = input.parseCards()
            .map { it.getValue().second }.toMutableList()
        val cardNumbers = cards.mapIndexed { index, _ -> index to 1 }.toMap(mutableMapOf())

        interim.indices.forEach { index ->
            val count = interim[index]
            (1 .. count).forEach { i ->
                cardNumbers[index + i] = cardNumbers[index + i]!! + (cardNumbers[index] ?: 0)
            }
        }

        return cardNumbers.values.sum()
    }

}

fun main() {
    Day04().execute().printResults()
}

private fun List<String>.parseCards(): List<Pair<List<Int>, List<Int>>> {
    return map {
        Pair(
            it.substring(it.indexOf(":") + 1, it.indexOf("|")).toIntList(),
            it.substring(it.indexOf("|") + 1).toIntList()
        )
    }
}

private fun Pair<List<Int>, List<Int>>.getValue(): Pair<Int, Int> {
    var result = 1
    var count = 0
    val left = ArrayDeque<Int>()
    left.addAll(first.sorted())
    val right = ArrayDeque<Int>()
    right.addAll(second.sorted())

    while (right.isNotEmpty() && left.isNotEmpty()) {
        val checkNumber = right[0]
        if (checkNumber == left[0]) {
            result = result shl 1
            left.removeFirst()
            right.removeFirst()
            count++
            continue
        }
        if (checkNumber < left[0]) {
            right.removeFirst()
            continue
        }
        if (checkNumber > left[0]) {
            left.removeFirst()
            continue
        }
    }
    return (result shr 1) to count
}
