package template

import aoc.common.Day
import aoc.common.printResults
import utils.toIntList

class Day04 : Day(year = 2023, day = 4, test = 13 to null) {
    override fun part1(input: List<String>): Any {
        return input.parseCards()
            .sumOf { it.getValue() }
    }

    override fun part2(input: List<String>): Any {
        return -1L
    }

}

fun main() {
    Day04().execute().printResults()
}
private fun List<String>.parseCards(): List<Pair<List<Int>, List<Int>>> {
    return map {
        Pair(it.substring(it.indexOf(":") + 1, it.indexOf("|")).toIntList(),
            it.substring( it.indexOf("|") + 1).toIntList())
    }
}

private fun Pair<List<Int>, List<Int>>.getValue(): Int {
    var result = 1
    var found = false
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
            found = true
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
    return result shr 1
}
