package aoc.years.y2025

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import kotlin.math.abs

private val testInput = """
    7,1
    11,1
    11,7
    9,7
    9,5
    2,5
    2,3
    7,3
""".trimIndent()

class Day09 : Day(
    year = 2025,
    day = 9,
    part1 = Part(test = 50L, testInput = testInput),
    part2 = Part(test = 24L, testInput = testInput),
) {
    override fun part1(input: List<String>): Long {
        val seats = input.parseCoordinates()
        val max = seats.indices.flatMap { a ->
            seats.indices.map { b ->
                seats[a] to seats[b]
            }
        }
        return max.maxOf { (a, b) ->
            (abs(a.x - b.x) + 1) * (abs(a.y - b.y) + 1)
        }
    }

    override fun part2(input: List<String>): Long {
        val seats = input.parseCoordinates().let {
            it + it.first()
        }

        val sorted = seats.indices.flatMap { a ->
            seats.indices.map { b ->
                seats[a] to seats[b]
            }
        }.sortedByDescending { (a, b) -> (abs(a.x - b.x) + 1) * (abs(a.y - b.y) + 1) }.first {
            seats.withinBounds(it.first, it.second)
        }
        return sorted.let { (a, b) ->
            (abs(a.x - b.x) + 1) * (abs(a.y - b.y) + 1)
        }
    }
}

private fun List<String>.parseCoordinates(): List<Coordinate> = map {
    val (a, b) = it.split(",").map(String::toLong)
    (a to b).toCoordinate()
}

fun main() {
    Day09().execute().printResults()
}

private data class Coordinate(val x: Long, val y: Long)

private fun Pair<Long, Long>.toCoordinate() = Coordinate(first, second)
private fun Pair<Coordinate, Coordinate>.toRectangle(): List<Pair<Coordinate, Coordinate>> {
    val (x1, y1) = first
    val (x2, y2) = second

    val topLeft = (minOf(x1, x2) + 1 to minOf(y1, y2) + 1).toCoordinate()
    val bottomRight = (maxOf(x1, x2) - 1 to maxOf(y1, y2) - 1).toCoordinate()
    val topRight = (bottomRight.x to topLeft.y).toCoordinate()
    val bottomLeft = (topLeft.x to bottomRight.y).toCoordinate()

    return listOf(
        topLeft to topRight, topRight to bottomRight, bottomRight to bottomLeft, bottomLeft to topLeft
    )
}

private val cache = mutableMapOf<Pair<Coordinate, Coordinate>, Boolean>()
private fun sortedPair(a: Coordinate, b: Coordinate): Pair<Coordinate, Coordinate> =
    listOf(a, b).sortedWith(compareBy({ it.x }, { it.y })).let { it[0] to it[1] }
private fun List<Coordinate>.withinBounds(a: Coordinate, b: Coordinate) =
    cache.getOrPut(sortedPair(a, b)) {
        (a to b).toRectangle().all { line ->
            val (start, end) = line
            val direction = line.toDirection()
            windowed(2).asSequence().map { it[0] to it[1] }.filter{ it.toDirection() != direction }.none { other ->
                val (otherStart, otherEnd) = other
                val otherDirection = other.toDirection()
                when {
                    direction == Direction.HORIZONTAL && otherDirection == Direction.VERTICAL ->
                        (start to end) intersects (otherStart to otherEnd)

                    direction == Direction.VERTICAL && otherDirection == Direction.HORIZONTAL ->
                        (otherStart to otherEnd) intersects (start to end)

                    else -> false
                }
            }
    }
}

private infix fun Pair<Coordinate, Coordinate>.intersects(vertical: Pair<Coordinate, Coordinate>): Boolean {
    val (horizontalStart, horizontalEnd) = this
    val (verticalStart, verticalEnd) = vertical

    val xRange = minOf(horizontalStart.x, horizontalEnd.x)..maxOf(horizontalStart.x, horizontalEnd.x)
    val yRange = minOf(verticalStart.y, verticalEnd.y)..maxOf(verticalStart.y, verticalEnd.y)

    val xVertical = verticalStart.x
    val yHorizontal = horizontalStart.y

    return xVertical in xRange && yHorizontal in yRange
}

private enum class Direction {
    VERTICAL, HORIZONTAL
}

private fun Pair<Coordinate, Coordinate>.toDirection() =
    if (first.x == second.x) Direction.VERTICAL
    else Direction.HORIZONTAL
