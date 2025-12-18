package aoc.years.y2023

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import kotlin.math.pow

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
        pattern.moveNorth()
        return pattern.mapIndexed { index, types ->
            types.count { it == ObjectType.ROUND_ROCK } * (pattern.size - index)
        }.sum().toLong()
    }

    override fun part2(input: List<String>): Any {
        val pattern = input.parsePattern()
        val cache = mutableSetOf<List<Pair<Int, Int>>>()
        repeat(1000000000) {
            val currentRocks = pattern.getRocks()
            if (cache.contains(currentRocks)) {
                println("Cycle detected at iteration $it")
                var cycleSize = 0
                while(true) {
                    cycleSize += 1
                    pattern.cycle()
                    if (pattern.getRocks().containsAll(currentRocks)) {
                        break
                    }
                }
                repeat((1000000000 - it) % cycleSize) {
                    pattern.cycle()
                }
                return pattern.calcLoad()
            } else {
                cache.add(pattern.getRocks())
                it.takeIf { it % 1000000 == 0 }?.let {
                    println("Patternsize: ${pattern.size} Iteration $it, ${1000000000 - it} remaining")
                }
                pattern.cycle()
            }
        }
        return pattern.calcLoad()
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

private fun List<MutableList<ObjectType>>.moveNorth(): Boolean {
    var moved = false
    forEachIndexed { y, row ->
        row.forEachIndexed { x, type ->
            if (type == ObjectType.ROUND_ROCK) {
                // search north for empty space
                val ny = (y - 1 downTo 0).takeWhile { ty ->
                    this[ty][x] == ObjectType.EMPTY
                }.lastOrNull() ?: return@forEachIndexed
                this[ny][x] = ObjectType.ROUND_ROCK
                this[y][x] = ObjectType.EMPTY
                moved = true
            }
        }
    }
    return moved
}

private fun List<MutableList<ObjectType>>.moveSouth(): Boolean {
    var moved = false
    indices.reversed().forEach { y ->
        this[y].forEachIndexed { x, type ->
            if (type == ObjectType.ROUND_ROCK) {
                // search south for empty space
                val ny = (y + 1 until size).takeWhile { ty ->
                    this[ty][x] == ObjectType.EMPTY
                }.lastOrNull() ?: return@forEachIndexed
                this[ny][x] = ObjectType.ROUND_ROCK
                this[y][x] = ObjectType.EMPTY
                moved = true
            }
        }
    }
    return moved
}

private fun List<MutableList<ObjectType>>.moveWest(): Boolean {
    var moved = false
    forEachIndexed { y, row ->
        row.forEachIndexed { x, type ->
            if (type == ObjectType.ROUND_ROCK) {
                // search west for empty space
                val nx = (x - 1 downTo 0).takeWhile { tx ->
                    this[y][tx] == ObjectType.EMPTY
                }.lastOrNull()
                if (nx == null) {
                    // cannot move
                    return@forEachIndexed
                }
                this[y][nx] = ObjectType.ROUND_ROCK
                this[y][x] = ObjectType.EMPTY
                moved = true
            }
        }
    }
    return moved
}

private fun List<MutableList<ObjectType>>.moveEast(): Boolean {
    var moved = false
    forEachIndexed { y, row ->
        row.indices.reversed().forEach { x ->
            val type = this[y][x]
            if (type == ObjectType.ROUND_ROCK) {
                // search east for empty space
                val nx = (x + 1 until size).takeWhile { tx ->
                    this[y][tx] == ObjectType.EMPTY
                }.lastOrNull()
                if (nx == null) {
                    // cannot move
                    return@forEach
                }
                this[y][nx] = ObjectType.ROUND_ROCK
                this[y][x] = ObjectType.EMPTY
                moved = true
            }
        }
    }
    return moved
}

private fun Board.cycle() {
    moveNorth()
    moveWest()
    moveSouth()
    moveEast()
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
