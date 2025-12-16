package aoc.years.y2025

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

private val testInput = """
    ..@@.@@@@.
    @@@.@.@.@@
    @@@@@.@.@@
    @.@@@@..@.
    @@.@@@@.@@
    .@@@@@@@.@
    .@.@.@.@@@
    @.@@@.@@@@
    .@@@@@@@@.
    @.@.@@@.@.
""".trimIndent()

class Day04 : Day(
    year = 2025,
    day = 4,
    part1 = Part(test = 13L, testInput = testInput),
    part2 = Part(test = 43L, testInput = testInput),
) {
    override fun part1(input: List<String>): Long {
        return input.toGrid()
            .numRolls()
    }

    override fun part2(input: List<String>): Long {
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

private fun List<CharArray>.numRolls(): Long {
    var result = 0L
    iterateAndReturn { _, _ -> result += 1 }
    return result
}

private fun List<CharArray>.removeRolls(): Long {
    var totalRemoved = 0L
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
