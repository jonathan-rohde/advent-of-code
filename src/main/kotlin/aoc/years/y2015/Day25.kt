package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

fun main() {
    Day25().execute().printResults()
}

private val testInput = """
    To continue, please consult the code grid in the manual.  Enter the code at row 2, column 1.
""".trimIndent()

class Day25 : Day(
    year = 2015,
    day = 25,
    part1 = Part(test = 31916031L, testInput = testInput),
    part2 = Part(test = "Merry Christmas 2015", testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        val coords = input[0].parseCoord()

        return generateCodes(coords)
    }

    override fun part2(input: List<String>): Any {
        return "Merry Christmas 2015"
    }
}

private fun String.parseCoord(): Pair<Int, Int> {
    return Pair(
        dropWhile { !it.isDigit() }.takeWhile { it.isDigit() }.toInt(),
        dropLastWhile { !it.isDigit() }.takeLastWhile { it.isDigit() }.toInt())

}

private fun generateCodes(searchCoord: Pair<Int, Int>): Long {
    var currentCoord = (1 to 1)
    var currentCode = 20151125L

    while (currentCoord != searchCoord) {
        currentCode = (currentCode * 252533) % 33554393
        currentCoord = currentCoord.next()
    }

    return currentCode
}

private fun Pair<Int, Int>.next(): Pair<Int, Int> {
    val newCol = second + 1
    val newRow = first - 1

    if (newRow == 0) {
        return newCol to 1
    }
    return newRow to newCol
}
