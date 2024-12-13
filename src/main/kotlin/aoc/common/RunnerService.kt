package aoc.common

import kotlin.time.Duration
import kotlin.time.measureTime

class RunnerService(
    val days: List<Day>,
    val year: Int,
    val executeRuns: Int = 1,
    val opaqueResults: Boolean = false
) {

    fun execute(limit: List<Int> = emptyList()) {
        var stats: Result? = null
        println("#########\n# $year \n#########")
        println("Day | Min | Max | Avg | Median")
        val overall = measureTime {
            days.asSequence().filter { proc -> limit.isEmpty() || proc.day in limit }
                .map { it to it.execute(executeRuns) }
                .forEach { (proc, result) ->
                    if (stats == null) {
                        stats = result
                    } else {
                        stats = stats!! join result
                    }
                    print("Day ${proc.day} part 1 | ")
                    result.part1.print()
                    println()
                    print("Day ${proc.day} part 2 | ")
                    result.part2.print()
                    println()
                }
        }


        println("Overall | ${overall}")
    }

    private fun PartResult.print() {
        print(" $min | $max | $avg | $median" )
    }

    private fun Pair<Duration, Any>.printTestResult() {
        print(" $second | $first")
    }
}

private infix fun Result.join(other: Result): Result {
    return Result(
        testPart1 = testPart1,
        testPart2 = testPart2,
        part1 = part1 join other.part1,
        part2 = part2 join other.part2
    )
}

private infix fun PartResult.join(other: PartResult): PartResult {
    return PartResult(
        min = min.coerceAtMost(other.min),
        max = max.coerceAtLeast(other.max),
        avg = (avg + other.avg) / 2,
        median = (median + other.median) / 2,
        distinct = distinct + other.distinct
    )
}
