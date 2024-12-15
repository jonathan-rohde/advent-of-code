import utils.readInput
import utils.testAndPrint

fun main() {
    fun part1(input: List<String>): Long {
        val game = input.parseMap()
        val commands = input.parseCommands()
        game.execute(commands)
//        game.printMap()
        return game.sumGps()
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    val testInput = readInput("Day15_test")
    part1(testInput).testAndPrint(10092L)
    part2(testInput).testAndPrint()

    val input = readInput("Day15")
    part1(input).testAndPrint()
    part2(input).testAndPrint()
}

private fun List<String>.parseMap(): Game {
    var robotPosition = Pair(0, 0)
    val board = takeWhile { it.isNotBlank() }
        .mapIndexedTo(mutableListOf()) { y, it ->
            it.mapIndexedTo(mutableListOf()) { x, c ->
                val tile = c.parseTile()
                if (tile.type == TileType.ROBOT) {
                    robotPosition = Pair(x, y)
                }
                tile
            }
        }
    return Game(board, robotPosition)
}

private fun List<String>.parseCommands(): List<Move> {
    return dropWhile { it.isNotBlank() }
        .drop(1)
        .flatMap { it.map { c -> c.parseMove() } }
}

private fun Char.parseTile(): Tile {
    return when (this) {
        '#' -> Tile(type = TileType.WALL)
        'O' -> Tile(type = TileType.BOX)
        '@' -> Tile(type = TileType.ROBOT)
        '.' -> Tile(type = TileType.EMPTY)
        else -> throw IllegalArgumentException("Illegal character $this")
    }
}

private fun Char.parseMove(): Move {
    return when (this) {
        '>' -> Move.RIGHT
        '<' -> Move.LEFT
        'v' -> Move.DOWN
        '^' -> Move.UP
        else -> throw IllegalArgumentException("Illegal character $this")
    }
}

private fun Game.execute(commands: List<Move>) {
    commands.forEach { command ->
        execute(command)
    }
}

private fun Game.execute(command: Move) {
    val (x, y) = robotPosition
    val (robotNewX, robotNewY) = command.next(x, y)
    if (board[robotNewY][robotNewX].type == TileType.EMPTY) {
        robotPosition = Pair(robotNewX, robotNewY)
        board[y][x].type = TileType.EMPTY
        board[robotNewY][robotNewX].type = TileType.ROBOT
    } else if (board[robotNewY][robotNewX].type == TileType.BOX) {
        val boxPlacement = moveBox(command, robotNewX, robotNewY)
        if (boxPlacement != null) {
            val (boxX, boxY) = boxPlacement
            board[y][x].type = TileType.EMPTY
            board[robotNewY][robotNewX].type = TileType.ROBOT
            board[boxY][boxX].type = TileType.BOX
            robotPosition = Pair(robotNewX, robotNewY)
        }
    }
}

private fun Game.moveBox(command: Move, x: Int, y: Int): Pair<Int, Int>? {
    var next = command.next(x, y)
    while (board[next.second][next.first].type == TileType.BOX) {
        next = command.next(next.first, next.second)
    }
    if (board[next.second][next.first].type == TileType.EMPTY) {
        return next
    }
    return null
}

private fun Game.sumGps(): Long {
    return board.flatMapIndexed { y, tiles ->
        tiles.mapIndexed { x, tile ->
            if (tile.type == TileType.BOX) {
                gps(x, y)
            } else {
                0
            }
        }
    }.sum()
}

private fun gps(x: Int, y: Int): Long {
    return (y * 100 + x).toLong()
}

private fun Game.printMap() {
    board.forEachIndexed { y, rows ->
        rows.forEach {
            when (it.type) {
                TileType.WALL -> print("#")
                TileType.BOX -> print("O")
                TileType.EMPTY -> print(".")
                TileType.ROBOT -> print("@")
            }
        }
        kotlin.io.println()
    }
}

data class Game(
    val board: MutableList<MutableList<Tile>>,
    var robotPosition: Pair<Int, Int>
)

data class Tile(
    var type: TileType
)

enum class TileType {
    WALL,
    BOX,
    ROBOT,
    EMPTY
}

enum class Move {
    LEFT,
    RIGHT,
    DOWN,
    UP
}

private fun Move.next(x: Int, y: Int): Pair<Int, Int> {
    return when(this) {
        Move.LEFT -> Pair(x - 1, y)
        Move.RIGHT -> Pair(x + 1, y)
        Move.DOWN -> Pair(x, y + 1)
        Move.UP -> Pair(x, y - 1)
    }
}