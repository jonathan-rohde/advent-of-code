package aoc.common

import utils.println
import utils.readInput
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.measureTimedValue


abstract class Day(
    val year: Int,
    val day: Int,
    val test: Pair<Any?, Any?> = null to null,
    val testFile1: String = "Day${day.toString().padStart(2, '0')}_test",
    val testFile2: String = "Day${day.toString().padStart(2, '0')}_test"
) {

    fun execute(runs: Int = 1): Result {
        val dayString = day.toString().padStart(2, '0')
        val fileName = "Day$dayString"
        val testInput = readInput(testFile1, year)
        val testInput2 = readInput(testFile2, year)
        val input = readInput(fileName, year)

        val testResults = executePart1(testInput, test.first != null) to executePart2(testInput2, test.second != null)
        val results = (0 until runs).map {
            val part1 = executePart1(input)
            val part2 = executePart2(input)
            part1 to part2
        }
        return results join testResults
    }

    private fun executePart1(input: List<String>, test: Boolean = false) : Pair<Duration, Any> {
        val result = measureTimedValue { part1(input) }

        if (test) {
            check(result.value == this.test.first) {
                "${this.test.first} != $result"
            }
        }

        return result.duration to result.value
    }

    private fun executePart2(input: List<String>, test: Boolean = false) : Pair<Duration, Any> {
        val result = measureTimedValue { part2(input) }

        if (test) {
            check(result.value == this.test.second) {
                "${this.test.second} != $result"
            }
        }

        return result.duration to result.value
    }

    abstract fun part1(input: List<String>): Any
    abstract fun part2(input: List<String>): Any
}

data class Result(
    val testPart1: Pair<Duration, Any>,
    val testPart2: Pair<Duration, Any>,
    val part1: PartResult,
    val part2: PartResult
)

data class PartResult(
    val min: Duration,
    val max: Duration,
    val avg: Duration,
    val median: Duration,

    val distinct: Set<Any>
)

fun Result.printResults() {
    testPart1.second.println()
    testPart2.second.println()

    part1.distinct.joinToString(", ").println()
    part2.distinct.joinToString(", ").println()
}

internal infix fun List<Pair<Pair<Duration, Any>, Pair<Duration, Any>>>.join(testResults: Pair<Pair<Duration, Any>, Pair<Duration, Any>>) : Result {
    val part1 = map { it.first }
    val part2 = map { it.second }
    return Result(
        testPart1 = testResults.first,
        testPart2 = testResults.second,
        part1 = part1.statistics(),
        part2 = part2.statistics()
    )
}

private fun  List<Pair<Duration, Any>>.statistics(): PartResult {
    var min = Long.MAX_VALUE.nanoseconds
    var max = Long.MIN_VALUE.nanoseconds
    var sum = 0.0.nanoseconds
    map { it.first }.forEach { it ->
        if (it < min) min = it
        if (it > max) max = it
        sum += it

    }
    val avg = sum / size
    val median = map { it.first }.sorted()[size / 2]
    return PartResult(
        min = min,
        max = max,
        avg = avg,
        median = median,
        distinct = map { it.second }.toSet()
    )
}
