import utils.println
import utils.readInput

fun main() {
    fun part1(input: List<String>): Int {
        val map = parseMap(input)
        return map.solve()
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val testInput = readInput("Day06_test")
//    check(part1(testInput) == 1)
    part1(testInput).println()
//    part2(testInput).println()

    // Read the input from the `src/DayXX.txt` file.
    val input = readInput("Day06")
    part1(input).println()
//    part2(input).println()
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

private fun List<MutableList<Point>>.next(x: Int, y: Int): Pair<Pair<Int, Int>, Direction>? {
    if (!validate(x, y, this)) {
        return null
    }
    var direction = this[y][x].direction ?: throw IllegalArgumentException("Direction not found")
    var newPositions = when (direction) {
        Direction.NORTH -> Pair(x, y - 1)
        Direction.EAST -> Pair(x + 1, y)
        Direction.SOUTH -> Pair(x, y + 1)
        Direction.WEST -> Pair(x - 1, y)
    }
    if (validate(newPositions.first, newPositions.second, this)) {
        if (this[newPositions.second][newPositions.first].state == TILE.OBSTACLE) {
            direction = direction.turn()
            newPositions = when (direction) {
                Direction.NORTH -> Pair(x, y - 1)
                Direction.EAST -> Pair(x + 1, y)
                Direction.SOUTH -> Pair(x, y + 1)
                Direction.WEST -> Pair(x - 1, y)
            }
        }
    }

    return newPositions to direction
}

private fun List<MutableList<Point>>.solve(): Int {
    val map = this.toMutableList()
    var (x, y) = map.findPlayer()
    var visited = 0

    while(true) {
        val next = map.next(x, y) ?: break
        val coord = next.first
        val direction = next.second
        map[y][x].state = TILE.VISITED
        if (!validate(coord.first, coord.second, map)) {
            break
        }
        map[coord.second][coord.first].direction = direction
        visited++
        x = coord.first
        y = coord.second
    }

    return this.sumOf { row -> row.count { it.state == TILE.VISITED } }
}

private fun validate(x: Int, y: Int, map: List<List<Point>>) : Boolean{
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
    var direction: Direction? = null
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
