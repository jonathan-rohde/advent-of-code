package aoc.years.y2024

import aoc.common.Day
import aoc.common.printResults
import kotlin.math.min

class Day08 : Day(2024, 8, 14 to 34) {
    override fun part1(input: List<String>): Int {
        val playingMap = input.parseMap(checkDistance = true)
        playingMap.fillAntinodes()
        return playingMap.countFrequencies()
    }

    override fun part2(input: List<String>): Int {
        val playingMap = input.parseMap(checkDistance = false)
        playingMap.fillAntinodes()
        return playingMap.countFrequencies()
    }
}

fun main() {
    Day08().execute().printResults()
}

private fun PlayingMap.countFrequencies() = map.sumOf { row ->
    row.count {
        it.antinodes.isNotEmpty()
    }
}

private fun List<String>.parseMap(checkDistance: Boolean): PlayingMap {
    return PlayingMap(
        map = this.map { line ->
            line.map { char ->
                when (char) {
                    '.' -> Coord(null)
                    else -> Coord(char)
                }
            }
        },
        checkDistance = checkDistance
    )
}

private fun PlayingMap.fillAntinodes() {
    this.map.forEachIndexed { y, row ->
        row.forEachIndexed { x, coord ->
            if (coord.antenna != null) {
                val possiblePair: List<Pair<Int, Int>> = this.map.findPossiblePair(x, y, coord.antenna!!)
                possiblePair.forEach { pair ->
                    val locations = calculateLines(
                        pointA = Pair(x, y), pointB = pair, maxX = row.size - 1, maxY = this.map.size - 1,
                    )
                    locations.forEach {
                        this.map[it.second][it.first].setFrequency(coord.antenna!!, checkDistance)
                    }
                }

            }
        }
    }
}

private fun Coord.setFrequency(frequency: Char, checkDistance: Boolean) {
    if (antenna != frequency || !checkDistance) {
        antinodes.add(frequency)
    }
}

private fun PlayingMap.calculateLines(
    pointA: Pair<Int, Int>, pointB: Pair<Int, Int>,
    maxX: Int = 100, maxY: Int = 100
): List<Pair<Int, Int>> {
    val (x, y) = pointA
    val (x2, y2) = pointB
    val distanceX = x - x2
    val distanceY = y - y2

    val iterations = if (checkDistance) IntRange(1, 1) else IntRange(0, min(maxX, maxY))

    return iterations.flatMap {
        listOf(
            Pair(x + it * distanceX, y + it * distanceY),
            Pair(x2 - it * distanceX, y2 - it * distanceY)
        ).filter { (lx, ly) ->
            ly in map.indices && lx in map[0].indices
        }
    }
}

private fun List<List<Coord>>.findPossiblePair(x: Int, y: Int, frequency: Char): List<Pair<Int, Int>> {
    return flatMapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, coord ->
            Pair(colIndex, rowIndex) to coord.antenna
        }
            .filter { (coord, _) -> coord.first != x && coord.second != y }
            .filter { (_, antenna) -> antenna == frequency }
            .map { it.first }
    }
}

data class Coord(
    var antenna: Char?,
    var antinodes: MutableSet<Char> = mutableSetOf()
)

data class PlayingMap(
    val checkDistance: Boolean = true,
    val map: List<List<Coord>>
)
