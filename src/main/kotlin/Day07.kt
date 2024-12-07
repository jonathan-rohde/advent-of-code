import utils.println
import utils.readInput
import utils.toLongList
import kotlin.time.measureTimedValue

fun main() {
    fun part1(input: List<String>): Long {
        return input.filter { it.isNotEmpty() }.sumOf {
            val (sum, parts) = readEquation(it)
            val result = parts.reversed().equation(0, sum)
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
            val result = parts.reversed().equation(0, sum, concat = true)
            if (result.contains(sum)) {
                sum
            } else {
                0
            }
        }
    }

    val testInput = readInput("Day07_test")
    part1(testInput).let {
        println(it)
        check(it == 3749L) { it }
    }

    val input = readInput("Day07")
    measureTimedValue {
        part1(input).println()
    }.println()
    measureTimedValue {
        part2(input).println()
    }.println()
}

private fun readEquation(input: String): Pair<Long, List<Long>> {
    val (sum, parts) = input.split(":")
    return sum.toLong() to parts.trim().toLongList()
}

private fun List<Long>.equation(index: Int, target: Long, concat: Boolean = false): List<Long> {
    if (index == lastIndex) return listOf(get(index))

    val head = this[index]
    val mul = if (target % head == 0L) {
        val recursion = equation(index + 1, target / head, concat)
        recursion.map { it * head }
    } else emptyList()
    val sum = if (target - head >= 0) {
        val recursion = equation(index + 1, target - head, concat)
        recursion.map { it + head }
    } else emptyList()
    val concatList = if (concat && "$target".endsWith("$head")) {
        val newTarget = "$target".substring(0, "$target".length - "$head".length)
        if (newTarget.isNotEmpty()) {
            val recursion = equation(index + 1, newTarget.toLong(), true)
            recursion.map { "$it$head".toLong() }
        } else {
            emptyList()
        }
    } else emptyList()

    return sum + mul + concatList
}