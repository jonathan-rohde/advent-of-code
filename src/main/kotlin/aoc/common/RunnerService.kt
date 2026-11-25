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
                runCatching { day.execute() }
                    .onSuccess { result ->
                        printPart(day.day, 1, result.part1, result.testPart1)
                        printPart(day.day, 2, result.part2, result.testPart2)
                    }
                    .onFailure { error ->
                        println("Day ${day.day} | FAILED: ${error::class.simpleName}: ${error.message}")
                        RunStatus.recordFailure("$year day ${day.day}: ${error::class.simpleName}")
                    }
            }
        }
        println("Overall | $overall")
    }

    private fun printPart(day: Int, part: Int, result: PartResult, test: TestResult?) {
        print("Day $day part $part | ${result.formatTiming()}")
        test?.failure?.let { failure ->
            print(" (TEST FAILED)")
            if (!opaqueResults) {
                print(": $failure")
            }
            RunStatus.recordFailure("$year day $day part $part: test failed")
        }
        println()
    }
}
