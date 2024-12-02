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

    fun isSafeTolerant(input: String): Boolean {
        if (isSafe(input)) {
            return true
        }
        val levels = input.toIntList()
        levels.forEachIndexed { index, level ->
            val newLevels = levels.toMutableList()
            newLevels.removeAt(index)
            if (isSafe(newLevels.joinToString(" "))) {
                return true
            }

        }
        return false
    }

    fun part1(input: List<String>): Int {
        return input
            .filter { it.isNotEmpty() }
            .count {
                isSafe(it)
            }
    }

    fun part2(input: List<String>): Int {
        return input
            .filter { it.isNotEmpty() }
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
