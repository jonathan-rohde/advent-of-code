package aoc.years.y2025

import aoc.common.Day
import aoc.common.printResults

class Day06 : Day(year = 2025, day = 6, test = 4277556L to 3263827L) {
    override fun part1(input: List<String>): Long {
        val grid = input.toGrid()
        return grid[0].indices.sumOf { i ->
            val operator = grid.last()[i]
            val default = when(operator) {
                "+" -> 0L
                "*" -> 1L
                else -> throw IllegalArgumentException("Invalid operator")
            }
            grid.dropLast(1)
                .fold(default) { acc, row ->
                    val value = row[i].toInt()
                    when (operator) {
                        "+" -> acc + value
                        "*" -> acc * value
                        else -> throw IllegalArgumentException("Invalid operator")
                    }
                }
        }
    }

    override fun part2(input: List<String>): Long {
        val grid = input.toInverseGrid()
        return grid.fold(0L) { acc, pair ->
                val (operator, numbers) = pair
                val value = when (operator) {
                    Operator.ADD -> numbers.sum()
                    Operator.MULTIPLY -> numbers.fold(1L) { prod, n -> prod * n }
                }
                acc + value

            }
    }
}

fun main() {
    Day06().execute().printResults()
}

private enum class Operator {
    ADD, MULTIPLY
}

private fun List<String>.toGrid(): List<List<String>> =
    map { row -> row.split(" +".toRegex()).filter { it.isNotEmpty() } }

private fun List<String>.toInverseGrid(): List<Pair<Operator, List<Long>>> {
    val grid = mutableListOf<Pair<Operator, List<Long>>>()

    with (map { row -> "$row " }) {
        var start = 0
        this[0].indices.drop(1).forEach { i ->
            if (this.dropLast(1).all { it[i] == ' ' }) {
                // numbers from start to i-1
                grid.add(numberAtCols(start, i - 1))
                start = i + 1
            }
        }
        grid.add(numberAtCols(start, this[0].length - 1))
    }

    return grid
}

private fun List<String>.numberAtCols(startCol: Int, endCol: Int): Pair<Operator, List<Long>> {
    val result = mutableListOf<Long>()
    (endCol downTo startCol).forEach { colIndex ->
        var num = ""
        (lastIndex - 1 downTo 0 ).forEach { rowIndex ->
            num = this[rowIndex][colIndex] + num
        }
        result += num.trim().toLong()
    }
    if (result.isEmpty()) {
        return Operator.ADD to emptyList()
    }
    val operator = when(this[lastIndex].substring(startCol, startCol + 1).trim()) {
        "+" -> Operator.ADD
        "*" -> Operator.MULTIPLY
        else -> throw IllegalArgumentException("Invalid operator")
    }
    return operator to result
}
