package aoc.common

import aoc.common.downloader.answerProvider
import aoc.common.downloader.inputDataProvider
import kotlin.time.Duration
import kotlin.time.TimedValue
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
) {

    fun execute(runs: Int = ExecutionSettings.runs): Result {
        require(runs > 0) { "runs must be positive, was $runs" }

        val tests = executeTests()

        val input = inputDataProvider.getInputData(year, day)
        val part1Result = runPart(input, part1?.limit ?: 0, runs, ::part1).also { it.warn(1) }
        val part2Result = runPart(input, part2?.limit ?: 0, runs, ::part2).also { it.warn(2) }

        val answers = if (ExecutionSettings.verify) answerProvider.getAnswers(year, day) else null

        return Result(
            testPart1 = tests.testPart1,
            testPart2 = tests.testPart2,
            part1 = part1Result,
            part2 = part2Result,
            answerPart1 = answers?.let { checkAnswer(part1Result, it.part1) },
            answerPart2 = answers?.let { checkAnswer(part2Result, it.part2) }
        )
    }

    private fun checkAnswer(result: PartResult, expected: String?): AnswerCheck {
        val actual = result.value.toString()
        val failure = if (expected == null || actual == expected) null else "expected $expected but was $actual"
        return AnswerCheck(expected = expected, failure = failure)
    }

    fun executeTests(): TestResults = TestResults(
        testPart1 = part1?.let { runTest(it, ::part1) },
        testPart2 = part2?.let { runTest(it, ::part2) },
    )

    private fun runTest(part: Part, partFunction: (List<String>, Int) -> Any): TestResult? {
        val expected = part.test ?: return null
        val (value, duration) = measureTimedValue { partFunction(part.testInput.toInputLines(), part.testLimit) }
        val failure = if (value == expected) {
            null
        } else {
            "expected $expected (${expected.typeName()}) but was $value (${value.typeName()})"
        }
        return TestResult(value = value, duration = duration, failure = failure)
    }

    private fun runPart(input: List<String>, limit: Int, runs: Int, partFunction: (List<String>, Int) -> Any): PartResult =
        List(runs) { measureTimedValue { partFunction(input, limit) } }.toPartResult()

    private fun PartResult.warn(part: Int) {
        when (value) {
            is Int -> errorPrintln("Day $day, Part $part 🔥 Part returned Int, consider using Long to avoid overflow.")
            is Float -> errorPrintln("Day $day, Part $part 🔥 Part returned Float, consider using Double or BigDecimal to avoid overflow.")
        }
        if (distinct.size > 1) {
            errorPrintln("Day $day, Part $part ⚠️ Part returned ${distinct.size} different results across $runs runs.")
        }
    }

    private fun Any.typeName(): String = this::class.simpleName ?: "unknown type"

    open fun part1(input: List<String>): Any = -1L
    open fun part2(input: List<String>): Any = -1L

    open fun part1(input: List<String>, limit: Int): Any = part1(if (limit > 0) input.take(limit) else input)
    open fun part2(input: List<String>, limit: Int): Any = part2(if (limit > 0) input.take(limit) else input)
}

data class TestResults(
    val testPart1: TestResult?,
    val testPart2: TestResult?
)

data class TestResult(
    val value: Any,
    val duration: Duration,
    val failure: String?
)

data class AnswerCheck(
    val expected: String?,
    val failure: String?
)

data class PartResult(
    val runs: Int,
    val min: Duration,
    val max: Duration,
    val avg: Duration,
    val median: Duration,
    val distinct: Set<Any>
) {
    val value: Any get() = distinct.first()

    fun formatTiming(): String =
        if (runs == 1) "$min" else "min $min | avg $avg | median $median | max $max ($runs runs)"
}

private fun List<TimedValue<Any>>.toPartResult(): PartResult {
    val durations = map { it.duration }.sorted()
    val middle = durations.size / 2
    val median = if (durations.size % 2 == 1) durations[middle] else (durations[middle - 1] + durations[middle]) / 2
    return PartResult(
        runs = size,
        min = durations.first(),
        max = durations.last(),
        avg = durations.fold(Duration.ZERO, Duration::plus) / size,
        median = median,
        distinct = mapTo(linkedSetOf()) { it.value }
    )
}

data class Result(
    val testPart1: TestResult?,
    val testPart2: TestResult?,
    val part1: PartResult,
    val part2: PartResult,
    val answerPart1: AnswerCheck? = null,
    val answerPart2: AnswerCheck? = null
)

fun Result.printResults() {
    println("""
        -------- Results --------
        Test Part 1: ${testPart1.format()}
        Test Part 2: ${testPart2.format()}

        Part 1: ${part1.value} (${part1.formatTiming()})${answerPart1.format()}
        Part 2: ${part2.value} (${part2.formatTiming()})${answerPart2.format()}
    """.trimIndent())
}

private fun AnswerCheck?.format(): String = when {
    this == null -> ""
    expected == null -> " [no accepted answer]"
    failure == null -> " [✅ accepted answer]"
    else -> " [❌ $failure]"
}

private fun TestResult?.format(): String = when {
    this == null -> "none"
    failure == null -> "$value ($duration)"
    else -> "$value ($failure) ($duration)"
}
