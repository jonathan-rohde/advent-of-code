import utils.measured
import utils.readInput
import utils.testAndPrint
import java.util.*
import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Long {
        val maze = input.parseMaze()
        maze.initGraph()
        val distances = maze.dijkstra()
        return distances[maze.end]!!.toLong()
    }

    fun part2(input: List<String>): Long {
        val maze = input.parseMaze()
        maze.initGraph()
        val distances = maze.dijkstra()
        val seats = maze.countSeats(maze.end, distances).distinct()
        maze.printMaze(seats = seats)
        return seats.size.toLong()
    }

    val testInput = readInput("Day16_test")
    part1(testInput).testAndPrint()
    part2(testInput).testAndPrint()

    val input = readInput("Day16")
    measured(1) {
        part1(input).testAndPrint()
    }
    measured(2) {
//        part2(input).testAndPrint()
    }
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
                print(distances[i]!!/ 10000)
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
        if (north in cells.indices && cells[north]) {
            graphNorth[index] = graphNorth.getOrDefault(index, emptyList()) + north
        }
        if (south in cells.indices && cells[south]) {
            graphSouth[index] = graphSouth.getOrDefault(index, emptyList()) + south
        }
        if (east in cells.indices && cells[east]) {
            graphEast[index] = graphEast.getOrDefault(index, emptyList()) + east
        }
        if (west in cells.indices && cells[west]) {
            graphWest[index] = graphWest.getOrDefault(index, emptyList()) + west
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
            listOf(
                graphWest, graphNorth, graphEast, graphSouth
            ).forEach {graph ->
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
    }
    return distances
}

private fun Maze.countSeats(cur: Int, distances: Map<Int, Int>, visited: Set<Int> = emptySet()): List<Int> {

//    return emptyList()
    if (cur == start) {
        return listOf(start)
    }

    val adjacents = listOf(
        cur - width,
        cur + width,
        cur - 1,
        cur + 1
    ).filter { it in cells.indices && cells[it] }
        .map { Pair(it, distances.getValue(it)) }
    val min = adjacents.minOfOrNull { it.second }!!
    val alternativeMin = if (adjacents.size > 2) min + 1000 else min
    val possible = adjacents.filter { it.second == min || it.second == alternativeMin }

    val predecessors = possible.map { it.first }
        .filter { !visited.contains(it) }
        .flatMap { countSeats(it, distances, visited + cur) }
    return predecessors + cur

//    if (visited.contains(cur)) {
//        return 0
//    }
//    val adjacents = listOf(
//        cur - width,
//        cur + width,
//        cur - 1,
//        cur + 1
//    ).filter { it in cells.indices && cells[it] }
//        .map {
//            val dir = directionTo(cur, it)
//            val weight = if (dir == direction) 1 else 1001
//        }
//    val min = adjacents.minOfOrNull { distances.getValue(it) }
//    if (min != null) {
//        val possible = adjacents.filter { distances.getValue(it) == min }
//        val rec = possible.sumOf { countSeats(it, distances, visited + cur) }
//        return rec + cur
//    }
//    return 0
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

    val graphEast: MutableMap<Int, List<Int>> = mutableMapOf(),
    val graphWest: MutableMap<Int, List<Int>> = mutableMapOf(),
    val graphNorth: MutableMap<Int, List<Int>> = mutableMapOf(),
    val graphSouth: MutableMap<Int, List<Int>> = mutableMapOf()
) {
}

private data class MazeCoord(
    val x: Int,
    val y: Int,
)
