package aoc.years.y2023

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import utils.gaussArea
import java.util.*

private val testInput = """
    ...........
    .S-------7.
    .|F-----7|.
    .||.....||.
    .||.....||.
    .|L-7.F-J|.
    .|..|.|..|.
    .L--J.L--J.
    ...........
""".trimIndent()

class Day10 : Day(
    year = 2023,
    day = 10,
    part1 = Part(test = 8, testInput = testInput),
    part2 = Part(test = 10, testInput = testInput)
) {
    override fun part1(input: List<String>): Any {
        val start = input.find("S")!!
        return input.findFarthest(start).first
    }

    override fun part2(input: List<String>): Any {
        val start = input.find("S")!!
        val path = input.findFarthest(start).second.map { it.first.toLong() to it.second.toLong() }
        input.indices.forEach { y ->
            input[y].indices.forEach { x ->
                if (path.contains(x.toLong() to y.toLong())) {
                    print("#")
                } else {
                    print(".")
                }
            }
            println()
        }
        return path.gaussArea(includeEdge = false)
    }

}

fun main() {
    Day10().execute().printResults()
}

private fun List<String>.find(s: String): Pair<Int, Int>? {
    forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            if (c.toString() == s) {
                return x to y
            }
        }
    }
    return null
}

private fun List<String>.filterValidNeighbours(candidates: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
    return candidates
        .filter { (x2, y2) ->
            y2 in indices && x2 in this[y2].indices
        }.filter { (x2, y2) ->
            this[y2][x2].toString() in listOf("|", "-", "L", "J", "7", "F", "S")
        }
}

private fun List<String>.filterValidFill(candidates: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
    return candidates
        .filter { (x2, y2) ->
            y2 in indices && x2 in this[y2].indices
        }.filter { (x2, y2) ->
            this[y2][x2].toString() in listOf("|", "-", "L", "J", "7", "F", "S", ".")
        }
}

private fun List<String>.findFarthest(pos: Pair<Int, Int>): Pair<Int, List<Pair<Int, Int>>> {
    val (x, y) = pos

    val queue = PriorityQueue<Triple<Pair<Int, Int>, Pair<Int, PipeWalkDirection>, List<Pair<Int, Int>>>>(compareByDescending { it.second.first })
    queue.add(Triple(x + 1 to y, 0 to PipeWalkDirection.RIGHT, emptyList()))
    queue.add(Triple(x - 1 to y, 0 to PipeWalkDirection.LEFT, emptyList()))
    queue.add(Triple(x to y + 1, 0 to PipeWalkDirection.DOWN, emptyList()))
    queue.add(Triple(x to y - 1, 0 to PipeWalkDirection.UP, emptyList()))
    val visited = mutableSetOf<Triple<Int, Int, PipeWalkDirection>>()

    var furthest = 0 to emptyList<Pair<Int, Int>>()

    while (queue.isNotEmpty()) {
        val (currentPos, traveledAndDirection, path) = queue.poll()
        val (traveled, direction) = traveledAndDirection
        if (currentPos.second !in indices || currentPos.first !in this[currentPos.second].indices) {
            continue
        }
        if (this[currentPos.second][currentPos.first] == 'S' && traveled > 0) {
            if (traveled > furthest.first) {
                furthest = (traveled + 1) to (path + pos)
            }
        }
        if (visited.add(Triple(currentPos.first, currentPos.second, direction))) {
            val (x2, y2) = currentPos
            val current = this[y2][x2]
            val neighbors = when(current) {
                'S' -> filterValidNeighbours(listOf(x2 to y2 + 1, x2 to y2 - 1, x2 + 1 to y2, x2 - 1 to y2))
                '|' -> filterValidNeighbours(listOf(x2 to y2 + 1, x2 to y2 - 1))
                '-' -> filterValidNeighbours(listOf(x2 + 1 to y2, x2 - 1 to y2))
                'L' -> filterValidNeighbours(listOf(x2 to y2 - 1, x2 + 1 to y2))
                'J' -> filterValidNeighbours(listOf(x2 to y2 - 1, x2 - 1 to y2))
                '7' -> filterValidNeighbours(listOf(x2 to y2 + 1, x2 - 1 to y2))
                'F' -> filterValidNeighbours(listOf(x2 to y2 + 1, x2 + 1 to y2))
                else -> continue
            }
            neighbors.forEach { queue.add(Triple(it, Pair(traveled + 1, direction), path + currentPos)) }
        }
    }
    return furthest.copy(
        first = furthest.first / 2
    )
}

private enum class PipeWalkDirection {
    UP, DOWN, LEFT, RIGHT
}
