import utils.measured
import utils.println
import utils.readInput
import utils.testAndPrint

fun main() {
    fun part1(input: List<String>, width: Int, height: Int): Long {
        val robots = robotSteps(input, 100, width, height)
            .groupBy {
                it.quadrant(width, height)
            }
            .filterKeys { it != null }

        return robots.mapValues { it.value.count() }
            .values
            .fold(1L, Long::times)

    }

    fun part2(input: List<String>, width: Int, height: Int): Long {
        return (1..width * height).reversed()
            .asSequence()
            .map {
                val robots = robotSteps(input, it, width, height).toSet()
                if (robots.containsChristmasTree()) {
                    robots.printMap(width, height)
                    it
                } else {
                    -1
                }
            }
            .filter { it != -1 }
            .firstOrNull()?.toLong() ?: -1
    }

    val testInput = readInput("Day14_test")
    part1(testInput, 11, 7).testAndPrint(12L)
    part2(testInput, 11, 7).testAndPrint()

    val input = readInput("Day14")
    part1(input, 101, 103).testAndPrint()
    measured { part2(input, 101, 103).testAndPrint() }.println()
}

private fun Collection<Pair<Int, Int>>.printMap(width: Int, height: Int) {
    for (y in 0 until height) {
        for (x in 0 until width) {
            count { it == Pair(x, y) }.let {
                if (it > 0) print(it)
                else print(".")
            }
        }
        kotlin.io.println()
    }
}

private fun Set<Pair<Int, Int>>.containsChristmasTree(): Boolean {
    val line = groupBy { it.second }
        .filter { it.value.size > 25 }
        .values.firstOrNull()?.sortedBy { it.first }
    if (line == null) return false
    var streak = 0
    line.drop(1).asSequence().forEachIndexed { index, pair ->
        if (line[index].first == pair.first - 1) {
            streak++
        } else {
            streak = 0
        }
        if (streak > 15) return true
    }
    return false
}

private fun robotSteps(input: List<String>, steps: Int, width: Int, height: Int): Sequence<Pair<Int, Int>> =
    input
        .asSequence()
        .filter { it.isNotBlank() }
        .map {
            it.parseRobot()
        }
        .map {
            Pair(it.startX + steps * it.vX, it.startY + steps * it.vY)
        }
        .map { (x, y) ->
            Pair(x.bounded(width), y.bounded(height))
        }

private fun String.parseRobot(): Robot {
    val match = "p=(-?[0-9]+),(-?[0-9]+) v=(-?[0-9]+),(-?[0-9]+)".toRegex().find(this) ?: error("Invalid robot")
    return Robot(
        match.groupValues[1].toInt(),
        match.groupValues[2].toInt(),
        match.groupValues[3].toInt(),
        match.groupValues[4].toInt(),
    )
}

private fun Int.bounded(len: Int): Int = (len + (this % len)) % len

private fun Pair<Int, Int>.quadrant(width: Int, height: Int): Int? {
    val (x, y) = this
    if (x == width / 2 || y == height / 2) {
        return null
    }
    return if (x < width / 2 && y < height / 2) {
        1
    } else if (x > width / 2 && y < height / 2) {
        2
    } else if (x < width / 2 && y > height / 2) {
        3
    } else if (x > width / 2 && y > height / 2) {
        4
    } else {
        null
    }
}

data class Robot(val startX: Int, val startY: Int, val vX: Int, val vY: Int)
