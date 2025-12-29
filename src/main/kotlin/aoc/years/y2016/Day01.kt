package aoc.years.y2016

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import kotlin.math.abs
import kotlin.text.fold

fun main() {
    Day01().execute().printResults()
}

class Day01 : Day(
    year = 2016,
    day = 1,
    part1 = Part(test = 12, testInput = "R5, L5, R5, R3"),
    part2 = Part(test = 4, testInput = "R8, R4, R4, R8"),
) {
    override fun part1(input: List<String>): Any {
        return input[0].toDirections().determineDistance().first
    }

    override fun part2(input: List<String>): Any {
        return input[0].toDirections().determineDistance().second
    }
}

private fun String.toDirections(): List<Pair<String, Int>> {
    return split(", ")
        .map {
            Pair(it.take(1), it.drop(1).toInt())
        }
}

private enum class StreetDirection {
    UP {
        override fun turn(dir: String): StreetDirection {
            return when(dir) {
                "L" -> LEFT
                "R" -> RIGHT
                else -> TODO()
            }
        }

    }, DOWN {
        override fun turn(dir: String): StreetDirection {
            return when(dir) {
                "L" -> RIGHT
                "R" -> LEFT
                else -> TODO()
            }
        }
    }, LEFT {
        override fun turn(dir: String): StreetDirection {
            return when(dir) {
                "L" -> DOWN
                "R" -> UP
                else -> TODO()
            }
        }
    }, RIGHT {
        override fun turn(dir: String): StreetDirection {
            return when(dir) {
                "L" -> UP
                "R" -> DOWN
                else -> TODO()
            }
        }
    };

    abstract fun turn(dir: String): StreetDirection
}
private fun List<Pair<String, Int>>.determineDistance(): Pair<Int, Int> {
    val hits = mutableMapOf<Pair<Int, Int>, Int>().withDefault { 0 }
    var bunnyHQ: Pair<Int, Int>? = null
    var position = 0 to 0
    var direction = StreetDirection.UP
    forEach { (naviDir, distance) ->
        direction = direction.turn(naviDir)
        val (newPos, doubleHit) = hits.walk(position, direction, distance)
        position = newPos

        if (doubleHit != null && bunnyHQ == null) {
            bunnyHQ = doubleHit
        }
    }

    return abs(position.first) + abs(position.second) to
            if (bunnyHQ != null) abs(bunnyHQ.first) + abs(bunnyHQ.second) else 0
}

private fun MutableMap<Pair<Int, Int>, Int>.walk(from: Pair<Int, Int>, direction: StreetDirection, distance: Int): Pair<Pair<Int, Int>, Pair<Int, Int>?> {
    var pos = from
    var check: Pair<Int, Int>? = null
    repeat(distance) {
        pos = when (direction) {
            StreetDirection.UP -> pos.first to pos.second - 1
            StreetDirection.DOWN -> pos.first to pos.second + 1
            StreetDirection.LEFT -> pos.first - 1 to pos.second
            StreetDirection.RIGHT -> pos.first + 1 to pos.second
        }
        this[pos] = getValue(pos) + 1
        if (this[pos] == 2 && check == null) {
            check = pos
        }
    }
    return pos to check
}
