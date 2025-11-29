package aoc.years.y2023

import aoc.common.Day
import aoc.common.printResults
import java.util.*
import kotlin.math.abs

class Day11 : Day(year = 2023, day = 11, test = 374L to null) {
    override fun part1(input: List<String>): Any {
        val expanded = input.expand()
        return expanded.countUniverse()
    }

    override fun part2(input: List<String>): Any {
        return -1
    }

}

fun main() {
    Day11().execute().printResults()
}

private fun List<String>.expand(): List<String> {
    val columns = this[0].indices.reversed().filter { colIndex ->
        val col = column(colIndex)
        col.all { it == '.' }
    }
    return flatMap { line ->
        val newLine = line.flatMapIndexed { colIndex, col ->
            if (colIndex in columns) {
                listOf(col, col)
            } else {
                listOf(col)
            }
        }.joinToString("")
        if (newLine.all { it == '.' }) {
            listOf(newLine, newLine)
        } else {
            listOf(newLine)
        }
    }
}

private fun List<String>.column(x: Int): String {
    return indices.joinToString("") { this[it][x].toString() }
}

private fun List<String>.countUniverse(): Long {
    val universes = findUniverse().combinations()
    return universes.sumOf { (u1, u2) ->
        abs(u1.first - u2.first) + abs(u1.second - u2.second)
    }.toLong()
}

private fun List<String>.findUniverse(): List<Pair<Int, Int>> {
    return mapIndexedNotNull { y, row ->
        row.mapIndexedNotNull { x, col ->
            if (col == '.') null
            else x to y
        }
    }.flatten()
}

private fun List<Pair<Int, Int>>.combinations(): List<Pair<Pair<Int, Int>, Pair<Int, Int>>> {
    val result = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
    forEach { one ->
        this.forEach { two ->
            if (!result.contains(one to two) && !result.contains(two to one) && one != two) {
                result.add(one to two)
            }
        }
    }
    return result
}