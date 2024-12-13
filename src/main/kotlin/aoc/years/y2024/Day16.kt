package aoc.years.y2024

import aoc.common.Day
import aoc.common.printResults
import java.util.*

class Day16 : Day(2024, 16, 11048L to 64L) {
    override fun part1(input: List<String>): Long {
        val maze = input.parseMaze()
        maze.initGraph()
        val distances = maze.dijkstra()
        return distances[maze.end]!!.toLong()
    }

    override fun part2(input: List<String>): Long {
        val maze = input.parseMaze()
        maze.initGraph()
        val seats = maze.countSeats()
        return seats.toLong()
    }
}

fun main() {
    Day16().execute().printResults()
}

private fun List<String>.parseMaze(): Maze {
    val width = this[0].length
    val height = this.size
    var start = -1
    var end = -1
    val cells = this.mapIndexedTo(mutableListOf()) { y, row ->
        row.mapIndexedTo(mutableListOf()) { x, cell ->
            when (cell) {
                'S' -> {
                    start = y * height + x
                    true
                }

                'E' -> {
                    end = y * height + x
                    true
                }

                else -> {
                    cell == '.'
                }
            }
        }
    }
    require(start >= 0)
    require(end >= 0)
    return Maze(cells.flatten(), start, end, width, height, mutableMapOf())
}

private fun Maze.printMaze(distances: Map<Int, Int> = emptyMap(), seats: List<Int> = emptyList()) {
    cells.forEachIndexed { i, entry ->
        if (i == start) {
            print("S")
        } else if (i == end) {
            print("E")
        } else {
            if (distances.contains(i)) {
                print(distances[i]!! / 10000)
            } else if (seats.contains(i)) {
                print("O")
            } else {
                print(if (entry) "." else "#")
            }
        }
        if (i % width == width - 1) {
            println()
        }
    }
}

private fun Maze.initGraph() {
    cells.forEachIndexed { index, cell ->
        val north = index - width
        val south = index + width
        val east = index + 1
        val west = index - 1
        listOf(north, south, east, west)
            .filter { it in cells.indices && cells[it] }
            .forEach {
                graph[index] = graph.getOrDefault(index, emptyList()) + it
            }
    }
}

private fun Maze.dijkstra(): Map<Int, Int> {
    val distances = mutableMapOf<Int, Int>().withDefault { Int.MAX_VALUE }
    val priorityQueue = PriorityQueue<Triple<Int, Int, Direction>>(compareBy { it.second })
    val visited = mutableSetOf<Pair<Int, Int>>()

    priorityQueue.add(Triple(start, 0, Direction.EAST))
    distances[start] = 0

    while (priorityQueue.isNotEmpty()) {
        val (node, currentDist, direction) = priorityQueue.poll()
        if (visited.add(node to currentDist)) {
            graph[node]?.forEach { adjacent ->
                val dir = directionTo(node, adjacent)
                val weight = if (dir == direction) 1 else 1001
                val totalDist = currentDist + weight
                if (totalDist <= distances.getValue(adjacent)) {
                    distances[adjacent] = totalDist
                    priorityQueue.add(Triple(adjacent, totalDist, dir))
                } else if (
                    totalDist == distances.getValue(adjacent)
                ) {
                    println("same $adjacent $totalDist")
                }
            }
        }
    }
    return distances
}

private fun Maze.countSeats(): Int {
    val queue: ArrayDeque<VisitData> = ArrayDeque<VisitData>()
    queue.add(VisitData(start, Direction.EAST, 0, mutableListOf()))
    queue.add(VisitData(start, Direction.NORTH, 1000, mutableListOf()))
    queue.add(VisitData(start, Direction.SOUTH, 1000, mutableListOf()))

    val scores = mutableMapOf<Int, Int>().withDefault { Int.MAX_VALUE }
    val seats = mutableListOf<VisitData>()

    while (queue.isNotEmpty()) {
        var (pos, direction, score, visited) = queue.poll()
        while (true) {
            visited += pos
            score += 1
            pos = direction.getNextPos(pos, width)
            if (!cells[pos] || visited.contains(pos) || scores.getValue(pos) < score - 1000) break

            queue.add(VisitData(pos, direction.turnLeft(), score + 1000, visited.toMutableList()))
            queue.add(VisitData(pos, direction.turnRight(), score + 1000, visited.toMutableList()))

            if (scores.getValue(pos) > score) {
                scores[pos] = score
            }

            if (pos == end) {
                seats.add(VisitData(pos, direction, score, visited.toMutableList()))
                break
            }
        }
    }
    return seats
        .filter { it.score == scores[end] }
        .flatMap { it.visited }
        .distinct()
        .count() + 1
}

private fun Direction.turnLeft(): Direction {
    return when (this) {
        Direction.NORTH -> Direction.WEST
        Direction.EAST -> Direction.NORTH
        Direction.SOUTH -> Direction.EAST
        Direction.WEST -> Direction.SOUTH
    }
}

private fun Direction.turnRight() = turnLeft().turnLeft().turnLeft()

private fun Direction.getNextPos(pos: Int, width: Int): Int {
    return when (this) {
        Direction.NORTH -> pos - width
        Direction.EAST -> pos + 1
        Direction.SOUTH -> pos + width
        Direction.WEST -> pos - 1
    }
}

private fun Maze.directionTo(from: Int, to: Int): Direction {
    val y = from / width
    val x = from % width
    val y2 = to / width
    val x2 = to % width
    return if (y == y2) {
        if (x2 < x) Direction.WEST else Direction.EAST
    } else {
        if (y2 < y) Direction.NORTH else Direction.SOUTH
    }
}

private data class Maze(
    val cells: List<Boolean>,
    val start: Int,
    val end: Int,
    val width: Int,
    val height: Int,

    val graph: MutableMap<Int, List<Int>> = mutableMapOf()
)

private data class VisitData(
    var pos: Int,
    val to: Direction,
    var score: Int,
    val visited: MutableList<Int>
)
