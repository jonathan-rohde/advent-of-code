import utils.measured
import utils.readInput
import utils.testAndPrint
import java.util.Comparator

fun main() {
    fun part1(input: List<String>): Long {
        val game = input.parseMap()
        val commands = input.parseCommands()
        game.execute(commands)
        return game.sumGps()
    }

    fun part2(input: List<String>): Long {
        val game = input.parseMap()
        game.expand()
        val commands = input.parseCommands()
        game.execute2(commands)
        return game.sumGps2()
    }

    val testInput = readInput("Day15_test")
    part1(testInput).testAndPrint(10092L)
    part2(testInput).testAndPrint(9021L)

    val input = readInput("Day15")
    measured(1) {  part1(input).testAndPrint() }
    measured(2) { part2(input).testAndPrint() }
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
        '#' -> Tile(type = TileType.WALL, false)
        'O' -> Tile(type = TileType.BOX, false)
        '@' -> Tile(type = TileType.ROBOT, false)
        '.' -> Tile(type = TileType.EMPTY, false)
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

private fun Game.execute2(commands: List<Move>) {
    commands.forEach { command ->
        execute2(command)
    }
}

private fun Game.execute2(command: Move) {
    val (x, y) = robotPosition
    val (robotNewX, robotNewY) = command.next(x, y)
    if (board[robotNewY][robotNewX].type == TileType.EMPTY) {
        robotPosition = Pair(robotNewX, robotNewY)
        board[y][x].type = TileType.EMPTY
        board[robotNewY][robotNewX].type = TileType.ROBOT
    } else if (board[robotNewY][robotNewX].type == TileType.BOX) {
        // move boxes
        val positionChanges = movingBoxes(command, x, y, robotNewX, robotNewY).distinct()
        if (positionChanges.isEmpty()) return
        positionChanges.forEach { change ->
            board[change.newY][change.newX].type = board[change.oldY][change.oldX].type
            board[change.newY][change.newX].open = board[change.oldY][change.oldX].open
            board[change.oldY][change.oldX].type = TileType.EMPTY
        }
        board[robotNewY][robotNewX].type = TileType.ROBOT
        board[y][x].type = TileType.EMPTY
        robotPosition = Pair(robotNewX, robotNewY)
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

private fun Game.movingBoxes(command: Move, fromX: Int, fromY: Int, x: Int, y: Int): List<PositionChange> {
    if (board[y][x].type == TileType.EMPTY) {
        return listOf(PositionChange(fromX, fromY, x, y))
    }
    if (board[y][x].type == TileType.WALL) {
        return emptyList()
    }
    val (left, right) = if (board[y][x].open) {
        Pair(x, y) to (Pair(x + 1, y))
    } else {
        Pair(x - 1, y) to Pair(x, y)
    }
    val leftNext = command.next(left.first, left.second)
    val rightNext = command.next(right.first, right.second)

    if (command == Move.LEFT) {
        val leftMove = movingBoxes(command, left.first, left.second, leftNext.first, leftNext.second)
        if (leftMove.isNotEmpty()) {
            val moves = leftMove + PositionChange(left.first, left.second, leftNext.first, leftNext.second) +
                    PositionChange(right.first, right.second, rightNext.first, rightNext.second)
            return moves.sortedBy { it.oldX }
        }
    } else if (command == Move.RIGHT) {
        val rightMove = movingBoxes(command, right.first, right.second, rightNext.first, rightNext.second)
        if (rightMove.isNotEmpty()) {
            val moves = rightMove + PositionChange(left.first, left.second, leftNext.first, leftNext.second) +
                PositionChange(right.first, right.second, rightNext.first, rightNext.second)
            return moves.sortedBy { it.oldX }.reversed()
        }
    } else if (command == Move.DOWN || command == Move.UP) {
        val leftMove = movingBoxes(command, left.first, left.second, leftNext.first, leftNext.second)
        val rightMove = movingBoxes(command, right.first, right.second, rightNext.first, rightNext.second)
        if (leftMove.isNotEmpty() && rightMove.isNotEmpty()) {
            val moves = leftMove + rightMove + listOf(
                PositionChange(left.first, left.second, leftNext.first, leftNext.second),
                PositionChange(right.first, right.second, rightNext.first, rightNext.second)
            )
            if (command == Move.DOWN) {
                return moves.sortedBy { it.oldY }.reversed()
            }
            return moves.sortedBy { it.oldY }
        }
    }

    return emptyList()
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

private fun Game.sumGps2(): Long {
    return board.flatMapIndexed { y, tiles ->
        tiles.mapIndexed { x, tile ->
            if (tile.type == TileType.BOX && tile.open) {
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

private fun Game.expand() {
    board = board.mapTo(mutableListOf()) { row ->
        row.flatMapTo(mutableListOf()) { tile ->
            when(tile.type) {
                TileType.WALL, TileType.EMPTY, TileType.BOX ->
                    listOf(Tile(tile.type, true), Tile(tile.type, false))
                TileType.ROBOT ->
                    listOf(Tile(TileType.ROBOT, tile.open), Tile(TileType.EMPTY, false))
            }
        }
    }
    robotPosition = Pair(robotPosition.first * 2, robotPosition.second)
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
        println()
    }
}

private fun Game.printMap2() {
    board.forEachIndexed { y, rows ->
        rows.forEachIndexed { x, it ->
            when (it.type) {
                TileType.WALL -> print("#")
                TileType.BOX -> if (board[y][x].open) {
                    print("[")
                } else {
                    print("]")
                }
                TileType.EMPTY -> print(".")
                TileType.ROBOT -> print("@")
            }
        }
        println()
    }
}

data class Game(
    var board: MutableList<MutableList<Tile>>,
    var robotPosition: Pair<Int, Int>
)

data class Tile(
    var type: TileType,
    var open: Boolean
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

data class PositionChange(
    val oldX: Int, val oldY: Int,
    val newX: Int, val newY: Int
)