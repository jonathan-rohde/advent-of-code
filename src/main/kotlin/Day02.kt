import utils.isSorted
import utils.println
import utils.readInput
import utils.toIntList
import kotlin.math.abs

fun main() {
    fun isSafe(input: List<Int>): Boolean {
        if (!input.isSorted()) return false
        val increment = input[0] < input[input.size - 1]
        val mistakes = input.zipWithNext().all {
            if (increment) {
                it.first < it.second && abs(it.first - it.second) <= 3
            } else {
                it.first > it.second && abs(it.first - it.second) <= 3
            }
        }
        return mistakes
    }

    fun isSafeTolerant(input: List<Int>): Boolean {
        if (isSafe(input)) {
            return true
        }
        input.forEachIndexed { index, _ ->
            val newLevels = input.toMutableList()
            newLevels.removeAt(index)
            if (isSafe(newLevels)) {
                return true
            }

        }
        return false
    }

    fun part1(input: List<String>): Int {
        return input
            .filter { it.isNotEmpty() }
            .map { it.toIntList() }
            .count {
                isSafe(it)
            }
    }

    fun part2(input: List<String>): Int {
        return input
            .filter { it.isNotEmpty() }
            .map { it.toIntList() }
            .count {
                isSafeTolerant(it)
            }
    }

    // Or read a large test input from the `src/DayXX_test.txt` file:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    // Read the input from the `src/main/resources/DayXX.txt` file.
    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
