package aoc.years.y2023

import aoc.common.Day
import aoc.common.printResults
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day11 : Day(year = 2023, day = 11, test = 374L to null) {
    override fun part1(input: List<String>): Any {
        val (rows, columns) = input.expandRowsAndCols()
        return input.countUniverse(rows, columns)
    }

    override fun part2(input: List<String>): Any {
        val (rows, columns) = input.expandRowsAndCols()
        return input.countUniverse(rows, columns, 999999)
    }

}

fun main() {
    Day11().execute().printResults()
}

private fun List<String>.expandRowsAndCols(): Pair<List<Int>, List<Int>> {
    val ys = this[0].indices.filter { colIndex ->
        val col = column(colIndex)
        col.none { it == '#' }
    }
    val xs = indices.filter { this[it].none { c -> c == '#' } }
    return xs to ys
}

private fun List<String>.column(x: Int): String {
    return indices.joinToString("") { this[it][x].toString() }
}

private fun List<String>.countUniverse(rows: List<Int>, columns: List<Int>, factor: Long = 1): Long {
    val universes = findUniverse().combinations()
    return universes.sumOf { (u1, u2) ->
        val overlaps = rows.overlaps(u1.y, u2.y) + columns.overlaps(u1.x, u2.x)
        val factorized = overlaps.toLong() * factor
        val dist = u1.distanceTo(u2)
        dist + factorized
    }
}

private fun List<String>.findUniverse(): List<UniverseCoord> {
    return mapIndexedNotNull { y, row ->
        row.mapIndexedNotNull { x, col ->
            if (col == '.') null
            else UniverseCoord(x, y)
        }
    }.flatten()
}

private fun List<UniverseCoord>.combinations(): List<Pair<UniverseCoord, UniverseCoord>> {
    val result = mutableSetOf<Pair<UniverseCoord, UniverseCoord>>()
    val data = sortedWith(compareBy({ it.y }, { it.x }))
    data.forEach { one ->
        data.forEach { two ->
            if (!result.contains(one to two) && !result.contains(two to one) && one != two) {
                result.add(one to two)
            }
        }
    }
    return result.toList()
}

private fun List<Int>.overlaps(start: Int, end: Int): Int {
    val min = min(start, end)
    val max = max(start, end)
    return dropWhile { it < min }.takeWhile { it <= max }.count()
}

private data class UniverseCoord(
    val x: Int,
    val y: Int,
) {
    fun distanceTo(other: UniverseCoord): Int {
        return abs(other.x - x) + abs(other.y - y)
    }
}