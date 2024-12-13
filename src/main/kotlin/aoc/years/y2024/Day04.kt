package aoc.years.y2024

import aoc.common.Day
import aoc.common.printResults

class Day04 : Day(2024, 4, 18 to 9) {
    override fun part1(input: List<String>): Int {
        val coords = input.findOccurrences('X')
        val horizontal = coords.sumOf { (row, col) ->
            horizontalCheck(col, input[row])
        }
        val vertical = coords.sumOf { (row, col) ->
            verticalCheck(row, col, input)
        }
        val diagonal = coords.sumOf { (row, col) ->
            diagonalCheck(row, col, input)
        }
        return horizontal + vertical + diagonal
    }

    override fun part2(input: List<String>): Int {
        val coords = input.findOccurrences('A')
        return coords.count { (row, col) ->
            isXmas(row, col, input)
        }
    }
}

fun main() {
    Day04().execute().printResults()
}

fun isXmas(row: Int, col: Int, input: List<String>): Boolean {
    if (row >= 1 && row <= input.size - 2 && col >= 1 && col <= input[0].length - 2) {
        val one = input[row - 1][col - 1] + input[row][col] + input[row + 1][col + 1]
        val two = input[row - 1][col + 1] + input[row][col] + input[row + 1][col - 1]
        return (one == "MAS" || one == "SAM") && (two == "MAS" || two == "SAM")
    }
    return false
}

fun List<String>.findOccurrences(char: Char): List<Pair<Int, Int>> {
    return this.flatMapIndexed { row, line ->
        line.mapIndexed { col, c ->
            if (c == char) {
                row to col
            } else {
                null
            }
        }.filterNotNull()
    }
}

fun horizontalCheck(col: Int, line: String): Int {
    var result = 0
    if (col < line.length - 3) {
        if (line.substring(col, col + 4) == "XMAS") {
            result++
        }
    }
    if (col >= 3) {
        if (line.substring(col - 3, col + 1) == "SAMX") {
            result++
        }
    }
    return result
}

fun verticalCheck(row: Int, col: Int, input: List<String>): Int {
    val line = mutableListOf(input[0][col])
    for (i in 1 until input.size) {
        line.add(input[i][col])
    }
    return horizontalCheck(row, line.joinToString(separator = ""))
}

fun diagonalCheck(row: Int, col: Int, input: List<String>): Int {
    var count = 0
    // check to lower right
    if (row < input.size - 3 && col < input[0].length - 3) {
        val word = input[row][col] + input[row + 1][col + 1] + input[row + 2][col + 2] + input[row + 3][col + 3]
        if (word == "XMAS") {
            count++
        }
    }

    // check to upper right
    if (row >= 3 && col < input[0].length - 3) {
        val word = input[row][col] + input[row - 1][col + 1] + input[row - 2][col + 2] + input[row - 3][col + 3]
        if (word == "XMAS") {
            count++
        }
    }

    // check to lower left
    if (row < input.size - 3 && col >= 3) {
        val word = input[row][col] + input[row + 1][col - 1] + input[row + 2][col - 2] + input[row + 3][col - 3]
        if (word == "XMAS") {
            count++
        }
    }

    // check to upper left
    if (row >= 3 && col >= 3) {
        val word = input[row][col] + input[row - 1][col - 1] + input[row - 2][col - 2] + input[row - 3][col - 3]
        if (word == "XMAS") {
            count++
        }
    }

    return count
}

operator fun Char.plus(other: Char): String {
    return this.toString() + other
}
