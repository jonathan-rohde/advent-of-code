package aoc.years.y2023

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

private val testInput = """
    O....#....
    O.OO#....#
    .....##...
    OO.#O....O
    .O.....O#.
    O.#..O.#.#
    ..O..#O..O
    .......O..
    #....###..
    #OO..#....
""".trimIndent()

class Day14 : Day(
    year = 2023,
    day = 14,
    part1 = Part(test = 136L, testInput = testInput),
    part2 = Part(test = 64L, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        val pattern = input.parsePattern()
        pattern.move(MoveDirection.NORTH)
        return pattern.mapIndexed { index, types ->
            types.count { it == ObjectType.ROUND_ROCK } * (pattern.size - index)
        }.sum().toLong()
    }

    override fun part2(input: List<String>): Any {
        val pattern = input.parsePattern()
        val cache = mutableSetOf<List<Pair<Int, Int>>>()
        var lastRocks: List<Pair<Int, Int>> = emptyList()
        val cycles = mutableMapOf<List<Pair<Int, Int>>, List<Pair<Int, Int>>>()
        repeat(1000000000) {
            val currentRocks = pattern.getRocks()
            if (cache.contains(currentRocks)) {
                println("Cycle detected at iteration $it")
                val cycleSize = cycles.getCycleSize(currentRocks)
                repeat((1000000000 - it) % cycleSize) {
                    pattern.cycle()
                }
                return pattern.calcLoad()
            } else {
                cache.add(pattern.getRocks())
                cycles[lastRocks] = currentRocks
                pattern.cycle()
            }
            lastRocks = currentRocks
        }
        return pattern.calcLoad()
    }

    private fun Map<List<Pair<Int, Int>>, List<Pair<Int, Int>>>.getCycleSize(start: List<Pair<Int, Int>>): Int {
        var size = 0
        var current = start
        while (true) {
            size += 1
            current = this[current] ?: break
            if (current == start) {
                break
            }
        }
        return size
    }

    private fun List<MutableList<ObjectType>>.getRocks(): List<Pair<Int, Int>> {
        val rocks = mutableListOf<Pair<Int, Int>>()
        forEachIndexed { y, row ->
            row.forEachIndexed { x, type ->
                if (type == ObjectType.ROUND_ROCK) {
                    rocks.add(Pair(x, y))
                }
            }
        }
        return rocks
    }

    private fun List<MutableList<ObjectType>>.calcLoad(): Long {
        return mapIndexed { index, types ->
            types.count { it == ObjectType.ROUND_ROCK } * (size - index)
        }.sum().toLong()
    }
}

fun main() {
    Day14().execute().printResults()
}

private enum class MoveDirection {
    NORTH,
    SOUTH,
    EAST,
    WEST,
}

private fun List<MutableList<ObjectType>>.move(moveDirection: MoveDirection) {
    val rowIndex = when (moveDirection) {
        MoveDirection.SOUTH -> indices.reversed()
        else -> indices
    }
    rowIndex.forEach { y ->
        val colIndex = when (moveDirection) {
            MoveDirection.EAST -> this[y].indices.reversed()
            else -> this[y].indices
        }
        colIndex.forEach { x ->
            val type = this[y][x]
            if (type == ObjectType.ROUND_ROCK) {
                val (nx, ny) = when (moveDirection) {
                    MoveDirection.NORTH -> Pair(x, (y - 1 downTo 0).takeWhile { ty -> this[ty][x] == ObjectType.EMPTY }.lastOrNull())
                    MoveDirection.SOUTH -> Pair(x, (y + 1 until size).takeWhile { ty -> this[ty][x] == ObjectType.EMPTY }.lastOrNull())
                    MoveDirection.WEST -> Pair((x - 1 downTo 0).takeWhile { tx -> this[y][tx] == ObjectType.EMPTY }.lastOrNull(), y)
                    MoveDirection.EAST -> Pair((x + 1 until size).takeWhile { tx -> this[y][tx] == ObjectType.EMPTY }.lastOrNull(), y)
                }
                if (nx != null && ny != null) {
                    this[ny][nx] = ObjectType.ROUND_ROCK
                    this[y][x] = ObjectType.EMPTY
                }
            }
        }
    }
}

private fun Board.cycle() {
    move(MoveDirection.NORTH)
    move(MoveDirection.WEST)
    move(MoveDirection.SOUTH)
    move(MoveDirection.EAST)
//    moveNorth()
//    moveWest()
//    moveSouth()
//    moveEast()
}

private typealias Board = List<MutableList<ObjectType>>
private enum class ObjectType {
    EMPTY,
    CUBE_ROCK,
    ROUND_ROCK,
}
private fun List<String>.parsePattern(): List<MutableList<ObjectType>> {
    return map { row ->
        row.map { char ->
            when (char) {
                '.' -> ObjectType.EMPTY
                '#' -> ObjectType.CUBE_ROCK
                'O' -> ObjectType.ROUND_ROCK
                else -> error("Unknown character: $char")
            }
        }.toMutableList()
    }
}
