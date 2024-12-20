import utils.readInput
import utils.testAndPrint
import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Long {
        val track = input.parseTrack()
        val cheats = track.findCheats()
        return cheats.filter { track.path.size - it > 100 }.size.toLong()
    }

    fun part2(input: List<String>): Long {
        val track = input.parseTrack()
        val cheats = track.findCheats(20)
        return cheats.filter { track.path.size - it >= 100 }.size.toLong()
    }

    val testInput = readInput("Day20_test")
    part1(testInput).testAndPrint()
    part2(testInput).testAndPrint()

    val input = readInput("Day20")
    part1(input).testAndPrint()
    part2(input).testAndPrint()
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
    var prev: Pair<Int, Int>? = null
    var current = start
    while (current != end) {
        val next = next(current, prev)
        path.add(next)
        prev = current
        current = next
    }
    return path
}

private fun List<String>.next(current: Pair<Int, Int>, prev: Pair<Int, Int>?): Pair<Int, Int> {
    val (x, y) = current
    val directions = listOf(x to y - 1, x + 1 to y, x to y + 1, x - 1 to y)
    return directions
        .filter { (x, y) -> x to y != prev }
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
