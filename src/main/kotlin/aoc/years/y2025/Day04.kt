package aoc.years.y2025

import aoc.common.Day
import aoc.common.printResults

class Day04 : Day(year = 2025, day = 4, test = 13 to 43) {
    override fun part1(input: List<String>): Int {
        return input.toGrid()
            .numRolls()
    }

    override fun part2(input: List<String>): Int {
        return input.toGrid()
            .removeRolls()
    }
}

fun main() {
    Day04().execute().printResults()
}

private fun List<String>.toGrid() = map { it.toCharArray() }

private fun List<CharArray>.iterateAndReturn(block: (x: Int, y: Int) -> Unit) {
    indices.forEach { y ->
        this[y].indices.forEach { x ->
            if (this[y][x] == '@') {
                val neighbours = this.getNeighbours(x, y)
                if (neighbours < 4) {
                    block(x, y)
                }
            }
        }
    }
}

private fun List<CharArray>.numRolls(): Int {
    var result = 0
    iterateAndReturn { _, _ -> result += 1 }
    return result
}

private fun List<CharArray>.removeRolls(): Int {
    var totalRemoved = 0
    while (true) {
        var removed = 0
        iterateAndReturn { x, y ->
            removed += 1
            this[y][x] = '.'
        }
        totalRemoved += removed
        if (removed == 0) {
            break
        }
    }
    return totalRemoved
}

private fun List<CharArray>.getNeighbours(x: Int, y: Int): Int {
    var neighbours = 0
    (y - 1..y + 1).forEach { ny ->
        if (ny in indices) {
            (x - 1..x + 1).forEach { nx ->
                if (nx in this[ny].indices && !(nx == x && ny == y)) {
                    neighbours += if (this[ny][nx] == '@') 1 else 0
                }
            }
        }

    }
    return neighbours
}
