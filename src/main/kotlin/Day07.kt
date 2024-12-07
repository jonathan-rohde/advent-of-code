import utils.println
import utils.readInput
import utils.toLongList

fun main() {
    fun part1(input: List<String>): Long {
        return input.filter { it.isNotEmpty() }.sumOf {
            val (sum, parts) = readEquation(it)
            val result = parts.reversed().equation()
            if (result.contains(sum)) {
                sum
            } else {
                0
            }
        }
    }

    fun part2(input: List<String>): Long {
        return input.filter { it.isNotEmpty() }.sumOf {
            val (sum, parts) = readEquation(it)
            val result = parts.reversed().equation(concat = true)
            if (result.contains(sum)) {
                sum
            } else {
                0
            }
        }
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 3749L)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}

private fun readEquation(input: String): Pair<Long, List<Long>> {
    val (sum, parts) = input.split(":")
    return sum.toLong() to parts.trim().toLongList()
}

private fun List<Long>.equation(concat: Boolean = false): List<Long> {
    if (size == 1) return listOf(get(0))

    val sublist = subList(1, size)
    val recursion = sublist.equation(concat = concat)
    val mul = recursion.map { it * get(0) }
    val sum = recursion.map { it + get(0) }
    val concatList = if (!concat) emptyList() else recursion.map { "$it${get(0)}".toLong() }
    return sum + mul + concatList
}