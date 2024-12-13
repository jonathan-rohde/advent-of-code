package aoc.years.y2024

import aoc.common.Day
import aoc.common.printResults
import kotlin.math.abs

class Day20 : Day(2024, 20, 5L to 285L) {
    override fun part1(input: List<String>): Long {
        val minSave = if (input.size > 100) 100 else 20
        val track = input.parseTrack()
        val cheats = track.findCheats()
        return cheats.filter { track.path.size - it >= minSave }.size.toLong()
    }

    override fun part2(input: List<String>): Long {
        val minSave = if (input.size > 100) 100 else 50
        val track = input.parseTrack()
        val cheats = track.findCheats(20)
        return cheats.filter { track.path.size - it >= minSave }.size.toLong()
    }
}

fun main() {
    Day20().execute().printResults()
}

private fun List<String>.parseTrack(): RaceTrack {
    val width = first().length
    val height = size
    val path = createPath()
    return RaceTrack(width, height, path)
}

private fun List<String>.createPath(): List<Pair<Int, Int>> {
    val start = find("S")
    val end = find("E")

    val path = mutableListOf(start)
    var current = start
    while (current != end) {
        current = next(current, path)
        path.add(current)
    }
    return path
}

private fun List<String>.next(current: Pair<Int, Int>, visited: List<Pair<Int, Int>>): Pair<Int, Int> {
    val (x, y) = current
    val directions = listOf(x to y - 1, x + 1 to y, x to y + 1, x - 1 to y)
    return directions
        .filter { (x, y) -> x to y !in visited }
        .filter { (x, y) -> y in indices && x in this[y].indices }
        .first { (x, y) -> this[y][x] == '.' || this[y][x] == 'E' }
}

private fun List<String>.find(s: String): Pair<Int, Int> {
    for (y in indices) {
        val x = this[y].indexOf(s)
        if (x != -1) return x to y
    }
    error("No $s found")
}

private fun RaceTrack.findCheats(maxCheatSize: Int = 2): List<Int> {
    val cheats = mutableListOf<Int>()
    path.forEachIndexed { index, (x, y) ->
        val cheatDestinations = findCheatDestinations(index, x, y, maxCheatSize)
        cheatDestinations.forEach { to ->
            val distanceCutted = to - index
            val lengthShortcut = abs(x - path[to].first) + abs(y - path[to].second)
            val trackLength = path.size - distanceCutted + lengthShortcut
            cheats.add(trackLength)
        }
    }
    return cheats
}

private fun RaceTrack.findCheatDestinations(start: Int, x: Int, y: Int, maxCheatSize: Int): List<Int> {
    val destinations = mutableListOf<Int>()
    path.forEachIndexed { index, (x2, y2) ->
        if (index > start) {
            val distance = abs(x - x2) + abs(y - y2)
            if (distance <= maxCheatSize) {
                destinations.add(index)
            }
        }
    }
    return destinations
}

private data class RaceTrack(
    val width: Int,
    val height: Int,
    val path: List<Pair<Int, Int>>
)
