import utils.println
import utils.readInput
import kotlin.time.measureTimedValue

fun main() {
    fun part1(input: List<String>): Int {
        val map = parseMap(input)
        return map.solve()
    }

    fun part2(input: List<String>): Int {
        val map = parseMap(input)
        val original = map.copy()
        map.solve()
        var possibleSolutions = 0
        val possiblePlacements = map.flatMapIndexed { y, points ->
            List(points.size) { x ->
                Pair(x, y)
            }
        }.toList()
        possiblePlacements.parallelStream().forEach { (x, y) ->
            val replacement = original.copy()
            if (replacement[y][x].state == TILE.EMPTY) {
                replacement[y][x].state = TILE.OBSTACLE
                val solution = replacement.solve()
                if (solution == -1) {
                    possibleSolutions++
                }
            }
        }
        return possibleSolutions
    }

    val testInput = readInput("Day06_test")
    val testOutput1 = part1(testInput)
    val testOutput2 = part2(testInput)
    testOutput1.println()
    check(testOutput1 == 41)
    measureTimedValue {
        testOutput1.println()
    }.println()
    measureTimedValue {
        testOutput2.println()
    }.println()

    // Read the input from the `src/DayXX.txt` file.
    val input = readInput("Day06")
    measureTimedValue {
        part1(input).println()
    }.println()
    measureTimedValue {
        part2(input).println()
    }.println()
}

private fun List<MutableList<Point>>.copy(): List<MutableList<Point>> {
    val copy = mutableListOf<MutableList<Point>>()
    forEach { row ->
        val newRow = mutableListOf<Point>()
        row.forEach { point ->
            newRow.add(point.copy(visitedDirections = mutableListOf()))
        }
        copy.add(newRow)
    }
    return copy.toList()
}

private fun parseMap(input: List<String>): List<MutableList<Point>> {
    return input.map {
        it.map { Point.fromChar(it) }.toMutableList()
    }
}

private fun List<List<Point>>.findPlayer(): Pair<Int, Int> {
    for (y in this.indices) {
        for (x in this[y].indices) {
            if (this[y][x].state == TILE.PLAYER) {
                return Pair(x, y)
            }
        }
    }
    throw IllegalArgumentException("Player not found")
}

private fun next(x: Int, y: Int, direction: Direction): Pair<Int, Int> {
    val newPositions = when (direction) {
        Direction.NORTH -> Pair(x, y - 1)
        Direction.EAST -> Pair(x + 1, y)
        Direction.SOUTH -> Pair(x, y + 1)
        Direction.WEST -> Pair(x - 1, y)
    }

    return newPositions
}

private fun List<MutableList<Point>>.solve(): Int {
    val map = this.toMutableList()
    var (x, y) = map.findPlayer()
    var direction = map[y][x].direction ?: throw IllegalArgumentException("Direction not found")
    var visited = 0

    while (true) {
        if (map[y][x].visitedDirections.contains(direction)) {
            return -1
        } else {
            map[y][x].visitedDirections.add(direction)
        }
        val next = next(x, y, direction)
        if (!validate(next.first, next.second, map)) {
            break
        }
        if (map[next.second][next.first].state == TILE.OBSTACLE) {
            direction = direction.turn()
        } else {
            visited++
            map[y][x].state = TILE.VISITED
            x = next.first
            y = next.second
        }
    }

    return visited - 3
}

private fun validate(x: Int, y: Int, map: List<List<Point>>): Boolean {
    if (y !in map.indices || x !in map[y].indices) {
        return false
    }
    return true
}

enum class Direction {
    NORTH, EAST, SOUTH, WEST;

    fun turn(): Direction = when (this) {
        NORTH -> EAST
        EAST -> SOUTH
        SOUTH -> WEST
        WEST -> NORTH
    }

    companion object {
        fun fromChar(char: Char): Direction = when (char) {
            '^' -> NORTH
            '>' -> EAST
            'v' -> SOUTH
            '<' -> WEST
            else -> throw IllegalArgumentException("Unknown direction: $char")
        }
    }
}

data class Point(
    var state: TILE,
    var direction: Direction? = null,
    var visitedDirections: MutableList<Direction> = mutableListOf(),
    var breaker: Boolean = false
) {
    companion object {
        fun fromChar(char: Char): Point = when (char) {
            '.' -> Point(TILE.EMPTY)
            '#' -> Point(TILE.OBSTACLE)
            '<', '^', '>', 'v' -> Point(TILE.PLAYER, Direction.fromChar(char))
            else -> throw IllegalArgumentException("Unknown tile: $char")
        }
    }

    override fun toString(): String {
        return when (state) {
            TILE.EMPTY -> "."
            TILE.OBSTACLE -> "#"
            TILE.VISITED -> "X"
            TILE.PLAYER -> direction.toString()
        }
    }
}

enum class TILE {
    EMPTY,
    OBSTACLE,
    VISITED,
    PLAYER;
}

private fun timed(block: () -> Unit) {
    val start = System.currentTimeMillis()
    block()
    val end = System.currentTimeMillis()
    println("Execution time: ${end - start}ms")
}
