package aoc.common

import aoc.common.downloader.inputDataProvider
import kotlin.time.Duration
import kotlin.time.measureTimedValue

data class Part(
    val test: Any? = null,
    val testInput: String,
    val testLimit: Int = 0,
    val limit: Int = 0
)

abstract class Day(
    val year: Int,
    val day: Int,
    val test: Pair<Any?, Any?>? = null to null,
    val part1: Part? = null,
    val part2: Part? = null,

//    val testFile1: String = "Day${day.toString().padStart(2, '0')}_test",
//    val testFile2: String = "Day${day.toString().padStart(2, '0')}_test",
//    val testLimit1: Int = 0,
//    val limit1: Int = 0,
//    val testLimit2: Int = 0,
//    val limit2: Int = 0
) {

    fun execute(): Result {
        val hasTests = part1?.test != null || part2?.test != null
        val (testResults, test1Result, test2Result) = if (hasTests) {
            val testInput = part1!!.testInput.trim().lines()
            val testInput2 = part2!!.testInput.trim().lines()
            val testResults = executePart(testInput, part1.test != null, part1, ::part1) to executePart(testInput2, part2.test != null, part2, ::part2)
            val test1Result = testResults.showTestResult(1)
            val test2Result = testResults.showTestResult(2)
            Triple(testResults, test1Result, test2Result)
        } else {
            Triple(null, null, null)
        }

        val nonNullpart1 = part1 ?: Part(test = test?.first, testInput = "")
        val nonNullPart2 = part2 ?: Part(test = test?.second, testInput = "")



        val input = inputDataProvider.getInputData(year, day)

        val part1 = executePart(input, false, nonNullpart1, ::part1)
        val part2 = executePart(input, false, nonNullPart2, ::part2)

        part1.first.second.warnOverflow(1)
        part2.first.second.warnOverflow(2)



        return Result(
            testPart1 = testResults?.first?.first,
            test1Result = test1Result,
            testPart2 = testResults?.second?.first,
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

    private fun executePart(input: List<String>, test: Boolean, part: Part, partFunction: (List<String>, Int) -> Any): Pair<Pair<Duration, Any>, String?> {
        val limit = if (test) part.testLimit else part.limit
        val result = measureTimedValue { partFunction(input, limit) }

        val testResultOutput = if (test) {
            check(result.value == part.test) {
                "expected ${part.test}"
            }
        } else null

        return (result.duration to result.value) to testResultOutput
    }

//    private fun executePart1(input: List<String>, test: Boolean = false) : Pair<Pair<Duration, Any>, String?> {
//        val limit = if (test) part1.testLimit else part1.limit
//        val result = measureTimedValue { part1(input, limit) }
//
//        val testResultOutput = if (test) {
//            check(result.value == part1.test) {
//                "expected ${part1.test}"
//            }
//        } else null
//
//        return (result.duration to result.value) to testResultOutput
//    }

    private fun check(condition: Boolean, lazyMessage: () -> String): String? {
        if (!condition) {
            return lazyMessage()
        }
        return null
    }

    open fun part1(input: List<String>, limit: Int): Any = part1(if (limit > 0) input.take(limit) else input)
    open fun part2(input: List<String>, limit: Int): Any = part2(if (limit > 0) input.take(limit) else input)

    open fun part1(input: List<String>): Any = -1L
    open fun part2(input: List<String>): Any = -1L
}

data class Result(
    val testPart1: Pair<Duration, Any>?,
    val testPart2: Pair<Duration, Any>?,
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
    val testPart1Text = if (test1Result != null) {
        "${testPart1?.second} $test1Result"
    } else {
        "${testPart1?.second}"
    }
    val testPart2Text = if (test2Result != null) {
        "${testPart2?.second} $test2Result"
    } else {
        "${testPart2?.second}"
    }
    println("""
        -------- Results --------
        Test Part 1: $testPart1Text (${testPart1?.first})
        Test Part 2: $testPart2Text (${testPart2?.first})
        
        Part 1: ${part1.second} (${part1.first})
        Part 2: ${part2.second} (${part2.first})
    """.trimIndent())
}
