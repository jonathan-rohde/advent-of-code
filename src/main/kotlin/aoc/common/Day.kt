package aoc.common

import utils.readInput
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.measureTimedValue


abstract class Day(
    val year: Int,
    val day: Int,
    val test: Pair<Any?, Any?> = null to null,
    val testFile1: String = "Day${day.toString().padStart(2, '0')}_test",
    val testFile2: String = "Day${day.toString().padStart(2, '0')}_test",
    val testLimit1: Int = 0,
    val limit1: Int = 0,
    val testLimit2: Int = 0,
    val limit2: Int = 0
) {

    fun execute(): Result {
        val dayString = day.toString().padStart(2, '0')
        val fileName = "Day$dayString"
        val testInput = readInput(testFile1, year)
        val testInput2 = readInput(testFile2, year)
        val input = readInput(fileName, year)

        val testResults = executePart1(testInput, test.first != null) to executePart2(testInput2, test.second != null)
        val part1 = executePart1(input)
        val part2 = executePart2(input)

        part1.first.second.warnOverflow(1)
        part2.first.second.warnOverflow(2)

        val test1Result = testResults.showTestResult(1)
        val test2Result = testResults.showTestResult(2)

        return Result(
            testPart1 = testResults.first.first,
            test1Result = test1Result,
            testPart2 = testResults.second.first,
            test2Result = test2Result,
            part1 = part1.first,
            part2 = part2.first
        )
    }

    private fun Any.warnOverflow(part: Int) {
        when (this) {
            is Int -> errorPrintln("Day $day, Part $part 🔥 Part returned Int, consider using Long to avoid overflow.")
            is Float -> errorPrintln("Day $day, Part $part 🔥 Part returned Float, consider using Double or BigDecimal to avoid overflow.")
        }
    }

    private fun Pair<Pair<Pair<Duration, Any>, String?>, Pair<Pair<Duration, Any>, String?>>.showTestResult(part: Int): String? {
        val hasTestFailure = if (part == 1) {
            this.first.second != null
        } else {
            this.second.second != null
        }

        if (!hasTestFailure) {
            return null
        }
        return "(${if (part == 1) this.first.second else this.second.second})"
    }

    private fun executePart1(input: List<String>, test: Boolean = false) : Pair<Pair<Duration, Any>, String?> {
        val limit = if (test) testLimit1 else limit1
        val result = measureTimedValue { part1(input, limit) }

        val testResultOutput = if (test) {
            check(result.value == this.test.first) {
                "expected ${this.test.first}"
            }
        } else null

        return (result.duration to result.value) to testResultOutput
    }

    private fun check(condition: Boolean, lazyMessage: () -> String): String? {
        if (!condition) {
            return lazyMessage()
        }
        return null
    }

    private fun executePart2(input: List<String>, test: Boolean = false) : Pair<Pair<Duration, Any>, String?> {
        val limit = if (test) testLimit2 else limit2
        val result = measureTimedValue { part2(input, limit) }

        val testResultOutput = if (test) {
            check(result.value == this.test.second) {
                "${this.test.second} != ${result.value}"
            }
        } else null

        return (result.duration to result.value) to testResultOutput
    }

    open fun part1(input: List<String>, limit: Int): Any = part1(if (limit > 0) input.take(limit) else input)
    open fun part2(input: List<String>, limit: Int): Any = part2(if (limit > 0) input.take(limit) else input)

    open fun part1(input: List<String>): Any = -1
    open fun part2(input: List<String>): Any = -1
}

data class Result(
    val testPart1: Pair<Duration, Any>,
    val testPart2: Pair<Duration, Any>,
    val test1Result: String? = null,
    val test2Result: String? = null,
    val part1: Pair<Duration, Any>,
    val part2: Pair<Duration, Any>
)

data class PartResult(
    val min: Duration,
    val max: Duration,
    val avg: Duration,
    val median: Duration,

    val distinct: Set<Any>
)

fun Result.printResults() {
    val testPart1 = if (test1Result != null) {
        "${testPart1.second} $test1Result"
    } else {
        "${testPart1.second}"
    }
    val testPart2 = if (test2Result != null) {
        "${testPart2.second} $test2Result"
    } else {
        "${testPart2.second}"
    }
    println("""
        -------- Results --------
        Test Part 1: $testPart1
        Test Part 2: $testPart2
        
        Part 1: ${part1.second}
        Part 2: ${part2.second}
    """.trimIndent())
}
