package aoc.years.y2023

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import kotlin.math.abs

private val testInput = """
    R 6 (#70c710)
    D 5 (#0dc571)
    L 2 (#5713f0)
    D 2 (#d2c081)
    R 2 (#59c680)
    D 2 (#411b91)
    L 5 (#8ceee2)
    U 2 (#caa173)
    L 1 (#1b58a2)
    U 2 (#caa171)
    R 2 (#7807d2)
    U 3 (#a77fa3)
    L 2 (#015232)
    U 2 (#7a21e3)
""".trimIndent()

class Day18 : Day(
    year = 2023,
    day = 18,
    part1 = Part(test = 62L, testInput = testInput),
    part2 = Part(test = 952408144115L, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        return input.mapToPath(::parsePart1).area()
    }

    override fun part2(input: List<String>): Any {
        return input.mapToPath(::parsePart2).area()
    }
}

fun main() {
    Day18().execute().printResults()
}
private typealias SnowHole = Pair<Long, Long>

private fun parsePart1(line: String, prev: SnowHole): SnowHole {
    val offset = line.dropWhile { !it.isDigit() }.takeWhile { it.isDigit() }.toLong()
    val (x, y) = when (line[0]) {
        'R' -> Pair(prev.first + offset, prev.second)
        'D' -> Pair(prev.first, prev.second + offset)
        'L' -> Pair(prev.first - offset, prev.second)
        'U' -> Pair(prev.first, prev.second - offset)
        else -> throw Exception("Unknown line")
    }
    return x to y
}

private fun parsePart2(line: String, prev: SnowHole): SnowHole {
    val group = offsetRegex.findAll(line).first().groupValues[1]
    val offset = group.take(5).hexToLong()
    val direction = group.last().digitToInt()
    val (x, y) = when (direction) {
        0 -> Pair(prev.first + offset, prev.second)
        1 -> Pair(prev.first, prev.second + offset)
        2 -> Pair(prev.first - offset, prev.second)
        3 -> Pair(prev.first, prev.second - offset)
        else -> throw Exception("Unknown line")
    }
    return x to y
}

private val offsetRegex = "\\(#([a-z0-9]{6})\\)".toRegex()
private fun List<String>.mapToPath(parser: String.(SnowHole) -> SnowHole): List<SnowHole> {
    val path = mutableListOf(SnowHole(0, 0))
    var current = SnowHole(0, 0)
    forEach { line ->
        current = line.parser(current)
        path.add(current)
    }
    return path
}

private fun List<SnowHole>.area(): Long {
    var sum1 = 0L
    var sum2 = 0L

    windowed(2).map {it[0] to it[1]}.forEach { (a, b) ->
        sum1 += a.first * b.second
        sum2 += a.second * b.first
    }

    val edge = windowed(2).map {it[0] to it[1]}.sumOf { (a, b) ->
        abs(a.first - b.first) + abs(a.second - b.second)
    } / 2 + 1

    return (abs(sum1 - sum2) / 2) + edge
}