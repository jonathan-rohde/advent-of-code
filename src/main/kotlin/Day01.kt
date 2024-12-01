import kotlin.math.abs

fun main() {

    fun parseLists(input: List<String>): Pair<List<Int>, List<Int>> {
        return input.map {
            val parts = it.split("\\s+".toRegex())
            parts[0].toInt() to parts[1].toInt()
        }
            .toList()
            .unzip()
    }

    fun part1(input: List<String>): Int {
        val (first, second) = parseLists(input)
            .let {
                it.first.sorted() to it.second.sorted()
            }
        return first.zip(second)
            .sumOf { (a, b) -> abs(a - b) }
    }

    fun part2(input: List<String>): Int {
        val (first, second) = parseLists(input)
        return first.sumOf { left ->
            left * second.count { right ->
                right == left
            }
        }
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 11)
    check(part2(testInput) == 31)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}