package aoc.years.y2023

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import java.util.*

private val testInput = """
    2413432311323
    3215453535623
    3255245654254
    3446585845452
    4546657867536
    1438598798454
    4457876987766
    3637877979653
    4654967986887
    4564679986453
    1224686865563
    2546548887735
    4322674655533
""".trimIndent()

class Day17 : Day(
    year = 2023,
    day = 17,
    part1 = Part(test = 102, testInput = testInput),
    part2 = Part(test = 51, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        val grid = input.parseGrid()
        val distances = grid.shortestPath(0, 0)

        return distances[grid[0].lastIndex to grid.lastIndex] ?: -1
    }
}

fun main() {
    Day17().execute().printResults()
}

private fun List<String>.parseGrid(): List<List<Int>> {
    return map { line ->
        line.map { it.digitToInt() }
    }
}

private enum class MapDirection {
    NORTH, SOUTH, EAST, WEST
}

private typealias Coord = Pair<Int, Int>

private fun List<List<Int>>.shortestPath(startX: Int, startY: Int): Map<Pair<Int, Int>, Int> {
    val distances = mutableMapOf<Pair<Int, Int>, Int>().withDefault { Int.MAX_VALUE }
    val priorityQueue = PriorityQueue<Triple<Coord, Int, List<MapDirection>>>(compareBy { it.second })
    val visited = mutableSetOf<Triple<Coord, Int, List<MapDirection>>>()

    priorityQueue.add(Triple(Coord(startX, startY), 0, listOf()))
    distances[Coord(startX, startY)] = 0

    while (priorityQueue.isNotEmpty()) {
        val (node, currentDist, directions) = priorityQueue.poll()
        val (x, y) = node
        if (visited.add(Triple(node, currentDist, directions.takeLast(3)))) {
            val neighbours = getNeighbours(x, y, directions.lastOrNull())

            val filteredNeighbours = neighbours.filter { (_, _, direction) ->
                directions.takeLastWhile { it == direction }.size < 3
            }
            filteredNeighbours.forEach { (coord, distance, direction) ->
                val totalDist = currentDist + distance
                if (totalDist <= distances.getValue(coord)) {
                    distances[coord] = totalDist
                    val list = (directions + direction)
                    priorityQueue.add(Triple(coord, totalDist, list))
                }
            }
        }
    }
    return distances
}

private fun List<List<Int>>.getNeighbours(x: Int, y: Int, origin: MapDirection?): List<Triple<Coord, Int, MapDirection>> {
    val candidates = listOf(
        Pair(x, y - 1) to MapDirection.NORTH,
        Pair(x + 1, y) to MapDirection.EAST,
        Pair(x, y + 1) to MapDirection.SOUTH,
        Pair(x - 1, y) to MapDirection.WEST,
    )
    val exclude180 = candidates.filter { (_, direction) ->
            when (origin) {
                MapDirection.NORTH -> direction != MapDirection.SOUTH
                MapDirection.SOUTH -> direction != MapDirection.NORTH
                MapDirection.EAST -> direction != MapDirection.WEST
                MapDirection.WEST -> direction != MapDirection.EAST
                else -> true
            }
        }
    val excludeOutside = exclude180.filter { (coord, _) -> coord.second in this.indices && coord.first in this[coord.second].indices }

    val result = excludeOutside.map { (coord, direction) ->
            val (x, y) = coord
            Triple(coord, this[y][x], direction)
        }
    return result
}
