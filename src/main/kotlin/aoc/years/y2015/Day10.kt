package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import kotlin.math.ceil

fun main() {
    Day10().execute().printResults()
}

private val testInput = """
    1211
""".trimIndent()

class Day10 : Day(
    year = 2015,
    day = 10,
    part1 = Part(test = 182376, testInput = testInput),
    part2 = Part(test = 2584304, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        return generateSequence(0 to input[0]) {
            if (it.first == 40) null
            else it.first + 1 to it.second.lookAndSay()
        }.last().second.length.also { println("finished part 1") }
    }

    override fun part2(input: List<String>): Any {
        return generateSequence(0 to input[0]) {
            if (it.first == 50) null
            else it.first + 1 to it.second.lookAndSay()
        }.last().second.length.also { println("finished part 2") }
    }
}

private fun String.lookAndSay(): String {
    return lookAndSay(0)
}

private fun String.lookAndSay(start: Int): String {
    val result = StringBuilder(ceil(length * 1.3).toInt())
    var index = start
    while (index in this.indices) {
        val check = this[index]
        var endMarker = index
        while (endMarker in indices && this[endMarker] == check) { endMarker++}
        val block = this.substring(index, endMarker).length
        result.append("$block$check")
        index += block
    }
    return result.toString()

}
