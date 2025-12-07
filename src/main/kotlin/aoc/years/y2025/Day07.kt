package aoc.years.y2025

import aoc.common.Day
import aoc.common.printResults

class Day07 : Day(year = 2025, day = 7, test = 21 to 40L) {
    override fun part1(input: List<String>) = input.countSplits()

    override fun part2(input: List<String>) = input.countBeams()
}

fun main() {
    Day07().execute().printResults()
}

private val List<String>.start: Pair<Int, Int>
    get() = 0 to this[0].indexOf('S')

private fun List<String>.countSplits(): Int {
    val beamIndex = mutableSetOf(start.second)
    return indices.drop(1).sumOf { row ->
        val (newBeamIndex, splitsRecursive) = nextBeams(beamIndex, row)
        beamIndex.clear()
        beamIndex.addAll(newBeamIndex)
        splitsRecursive
    }
}

private fun List<String>.nextBeams(
    beamIndex: Set<Int>,
    row: Int
): Pair<MutableSet<Int>, Int> {
    val newBeamIndex = mutableSetOf<Int>()
    val splits = beamIndex.sumOf { index ->
        moveRay(row, index, newBeamIndex)
    }
    return Pair(newBeamIndex, splits)
}

private fun List<String>.moveRay(
    row: Int,
    index: Int,
    newBeamIndex: MutableSet<Int>
): Int {
    val below = this[row][index]
    return when (below) {
        '.' -> newBeamIndex.add(index).let { 0 }
        '^' -> newBeamIndex.addAll(listOf(index - 1, index + 1)).let { 1 }
        else -> 0
    }
}

private fun List<String>.countBeams() =
    countBeams(start.first, start.second)

private val beams = mutableMapOf<Pair<Int, Int>, Long>()
private fun List<String>.countBeams(row: Int, col: Int): Long {
    return beams.getOrPut(row to col) {
        val nextRow = row + 1
        when (getNext(nextRow, col)) {
            '.' -> countBeams(nextRow, col) // move one down
            '^' -> countBeams(row, col - 1) + countBeams(row, col + 1) // follow beam split left and right
            null -> 1L // reached the bottom
            else -> throw IllegalArgumentException("Invalid map entry")
        }
    }
}

private fun List<String>.getNext(nextRow: Int, col: Int): Char? =
    this.getOrNull(nextRow)?.getOrNull(col)
