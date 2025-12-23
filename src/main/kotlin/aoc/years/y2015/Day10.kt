package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

fun main() {
    Day10().execute().printResults()
}

private val testInput = """
    111221
""".trimIndent()

class Day10 : Day(
    year = 2015,
    day = 10,
    part1 = Part(test = 6, testInput = testInput),
    part2 = Part(test = 6, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        return generateSequence(0 to input[0]) {
            if (it.first == 40) null
            else it.first + 1 to it.second.lookAndSay()
        }.last().second.length.also { println(it) }
    }

//    override fun part2(input: List<String>): Any {
//        return generateSequence(0 to input[0]) {
//            if (it.first == 50) null
//            else it.first + 1 to it.second.lookAndSay()
//        }.last().second.length.also { println(it) }
//    }
}

private fun String.lookAndSay(): String {
    var current = this
    var result = ""
    while (current.isNotBlank()) {
        val check = current.first()
        val block = current.takeWhile { it == check }.length
        result += "$block$check"
        current = current.drop(block)
    }
    return result

}
