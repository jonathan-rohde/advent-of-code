package aoc.common

import kotlin.time.measureTime

object RunStatus {
    private val _failures = mutableListOf<String>()
    val failures: List<String> get() = _failures

    internal fun recordFailure(description: String) {
        _failures += description
    }
}

class RunnerService(
    val days: List<Day>,
    val year: Int,
    val opaqueResults: Boolean = false
) {

    fun execute(limit: List<Int> = emptyList()) {
        println("#########\n# $year \n#########")
        println("Day | Time")
        val overall = measureTime {
            days.filter { limit.isEmpty() || it.day in limit }.forEach { day ->
                if (ExecutionSettings.testOnly) executeTests(day) else executeDay(day)
            }
        }
        println("Overall | $overall")
    }

    private fun executeDay(day: Day) {
        runCatching { day.execute() }
            .onSuccess { result ->
                printPart(day.day, 1, result.part1, result.testPart1, result.answerPart1)
                printPart(day.day, 2, result.part2, result.testPart2, result.answerPart2)
            }
            .onFailure { error -> printFailure(day, error) }
    }

    private fun executeTests(day: Day) {
        runCatching { day.executeTests() }
            .onSuccess { result ->
                printTest(day.day, 1, result.testPart1)
                printTest(day.day, 2, result.testPart2)
            }
            .onFailure { error -> printFailure(day, error) }
    }

    private fun printFailure(day: Day, error: Throwable) {
        println("Day ${day.day} | FAILED: ${error::class.simpleName}: ${error.message}")
        RunStatus.recordFailure("$year day ${day.day}: ${error::class.simpleName}")
    }

    private fun printTest(day: Int, part: Int, test: TestResult?) {
        if (test == null) {
            println("Day $day part $part | no test")
            return
        }
        print("Day $day part $part | test ${test.duration}")
        printTestFailure(day, part, test)
        println()
    }

    private fun printPart(day: Int, part: Int, result: PartResult, test: TestResult?, answer: AnswerCheck?) {
        print("Day $day part $part | ${result.formatTiming()}")
        test?.let { printTestFailure(day, part, it) }
        answer?.let { printAnswerCheck(day, part, it) }
        println()
    }

    private fun printAnswerCheck(day: Int, part: Int, answer: AnswerCheck) {
        when {
            answer.expected == null -> print(" | no accepted answer")
            answer.failure == null -> print(" | answer ✅")
            else -> {
                print(" (ANSWER MISMATCH)")
                if (!opaqueResults) {
                    print(": ${answer.failure}")
                }
                RunStatus.recordFailure("$year day $day part $part: answer mismatch")
            }
        }
    }

    private fun printTestFailure(day: Int, part: Int, test: TestResult) {
        val failure = test.failure ?: return
        print(" (TEST FAILED)")
        if (!opaqueResults) {
            print(": $failure")
        }
        RunStatus.recordFailure("$year day $day part $part: test failed")
    }
}
