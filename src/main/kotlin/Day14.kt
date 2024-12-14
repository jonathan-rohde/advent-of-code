import utils.readInput
import utils.testAndPrint
import kotlin.math.abs

fun main() {
    fun part1(input: List<String>, width: Int, height: Int): Long {
        val robots = robotSteps(input, 100, width, height)

        return robots.mapValues { it.value.count() }
            .values
            .fold(1L, Long::times)

    }

    fun part2(input: List<String>, width: Int, height: Int): Long {
        (1..width * height).forEach {
            val robots = robotSteps(input, it, width, height).values.flatten().toSet()
            val middle = width / 2
            val topMiddle = Pair(middle, 0)
            if (robots.containsChristmasTree(width, height)) {
            println("#### $it ####")
            robots.printMap(width, height)
//                return@forEach
            }
        }
        return -1
    }

    val testInput = readInput("Day14_test")
    part1(testInput, 11, 7).testAndPrint(12L)
    part2(testInput, 11, 7).testAndPrint()

    val input = readInput("Day14")
//    part1(input, 101, 103).testAndPrint()
    part2(input, 101, 103).testAndPrint()
}

private fun Set<Pair<Int, Int>>.printMap(width: Int, height: Int) {
    for (x in 0..width) {
        for (y in 0..height) {
            if (contains(Pair(x, y))) {
                print("#")
            } else {
                print(".")
            }
        }
        kotlin.io.println()
    }
    kotlin.io.println()
    kotlin.io.println()
}

private fun Set<Pair<Int, Int>>.containsChristmasTree(width: Int, height: Int): Boolean {
    forEach { (x, y) ->
        if (
            contains(Pair(x, y))
            && contains(Pair(x + 1, y))
            && contains(Pair(x + 1, y - 1))
            && contains(Pair(x + 2, y))
            && contains(Pair(x + 2, y - 2))
            && contains(Pair(x + 3, y))
            && contains(Pair(x + 3, y - 3))

        ) {
            return true
        }
    }
    return false
}

private fun robotSteps(input: List<String>, steps: Int, width: Int, height: Int): Map<Int?, List<Pair<Int, Int>>> =
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
        .groupBy {
            it.quadrant(width, height)
        }
        .filterKeys { it != null }

private fun String.parseRobot(): Robot {
    val match = "p=(-?[0-9]+),(-?[0-9]+) v=(-?[0-9]+),(-?[0-9]+)".toRegex().find(this) ?: error("Invalid robot")
    return Robot(
        match.groupValues[1].toInt(),
        match.groupValues[2].toInt(),
        match.groupValues[3].toInt(),
        match.groupValues[4].toInt(),
    )
}

private fun Int.bounded(len: Int): Int {
    return (len + (this % len)) % len
//    return if (this < 0) {
//        len + (this % len)
//    } else {
//        this % len
//    }
}

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
