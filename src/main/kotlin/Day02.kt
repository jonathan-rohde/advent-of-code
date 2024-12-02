import kotlin.math.abs

fun main() {
    fun isSafe(input: String): Boolean {
        val levels = input.toIntList()
        val increment = levels[0] < levels[levels.size - 1]
        val mistakes = input.toIntList().zipWithNext().all {
            if (increment) {
                it.first < it.second && abs(it.first - it.second) <= 3
            } else {
                it.first > it.second && abs(it.first - it.second) <= 3
            }
        }
        return mistakes
    }

    fun part1(input: List<String>): Int {
        return input
            .filter { it.isNotEmpty() }
            .count {
                isSafe(it)
            }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // Or read a large test input from the `src/DayXX_test.txt` file:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 2)

    // Read the input from the `src/main/resources/DayXX.txt` file.
    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
