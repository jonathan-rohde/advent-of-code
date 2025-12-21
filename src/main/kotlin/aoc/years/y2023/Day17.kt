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
    part2 = Part(test = 94, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        return input.parseGrid().shortestPath(0, 0)
    }

    override fun part2(input: List<String>): Any {
        return input.parseGrid().shortestPath(0, 0, min = 4, max = 10)
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

private fun List<List<Int>>.shortestPath(x: Int, y: Int, min: Int = 0, max: Int = 3): Int {
    val target = Coord(this[0].lastIndex, this.lastIndex)
    val queue = PriorityQueue<Triple<Coord, Pair<MapDirection, Int>, Int>>(compareBy { it.third })
    val visited = mutableMapOf<Triple<Coord, MapDirection, Int>, Int>().withDefault { Int.MAX_VALUE }

    queue.add(Triple(Coord(x+1, y), Pair(MapDirection.EAST, 1), 0))
    queue.add(Triple(Coord(x, y+1), Pair(MapDirection.SOUTH, 1), 0))

    while (queue.isNotEmpty()) {
        val (node, move, heat) = queue.poll()
        val (walkDirection, straights) = move
        val (nx, ny) = node

        val totalHeat = heat + this[node.second][node.first]
        if (node == target && straights >= min) return totalHeat

        val check = Triple(node, walkDirection, straights)
        if (visited.getValue(check) > totalHeat) {
            visited[check] = totalHeat

            getNeighbours(nx, ny, walkDirection, straights, min, max)
                .forEach { (next, walk, line) ->  queue.add(Triple(next, Pair(walk, line), totalHeat))}
        }
    }

    return 0
}

private fun List<List<Int>>.getNeighbours(x: Int, y: Int, origin: MapDirection, straights: Int, min: Int, max: Int): List<Triple<Coord, MapDirection, Int>> {
    val result = mutableListOf<Triple<Coord, MapDirection, Int>>()
    if (straights >= min) {
        result.add(turnLeft(x, y, origin).let { Triple(it.first, it.second, 1)})
        result.add(turnRight(x, y, origin).let { Triple(it.first, it.second, 1)})
    }
    if (straights < max) {
        result.add(straight(x, y, origin).let { Triple(it.first, it.second, straights + 1)})
    }

    return result.filter { (coord, _) -> coord.first in this[0].indices && coord.second in this.indices }
}

private fun turnLeft(x: Int, y: Int, origin: MapDirection) = when (origin) {
    MapDirection.NORTH -> Pair(Coord(x - 1, y), MapDirection.WEST)
    MapDirection.SOUTH -> Pair(Coord(x + 1, y), MapDirection.EAST)
    MapDirection.EAST -> Pair(Coord(x, y - 1), MapDirection.NORTH)
    MapDirection.WEST -> Pair(Coord(x, y + 1), MapDirection.SOUTH)
}

private fun turnRight(x: Int, y: Int, origin: MapDirection) = when (origin) {
    MapDirection.NORTH -> Pair(Coord(x + 1, y), MapDirection.EAST)
    MapDirection.SOUTH -> Pair(Coord(x - 1, y), MapDirection.WEST)
    MapDirection.EAST -> Pair(Coord(x, y + 1), MapDirection.SOUTH)
    MapDirection.WEST -> Pair(Coord(x, y - 1), MapDirection.NORTH)
}

private fun straight(x: Int, y: Int, origin: MapDirection) = when (origin) {
    MapDirection.NORTH -> Pair(Coord(x, y - 1), MapDirection.NORTH)
    MapDirection.SOUTH -> Pair(Coord(x, y + 1), MapDirection.SOUTH)
    MapDirection.EAST -> Pair(Coord(x + 1, y), MapDirection.EAST)
    MapDirection.WEST -> Pair(Coord(x - 1, y), MapDirection.WEST)
}