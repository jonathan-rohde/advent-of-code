package aoc.common

import kotlin.time.Duration
import kotlin.time.measureTime

class RunnerService(
    val days: List<Day>,
    val year: Int,
    val opaqueResults: Boolean = false
) {

    fun execute(limit: List<Int> = emptyList()) {
        println("#########\n# $year \n#########")
        println("Day | Time")
        val overall = measureTime {
            days.asSequence().filter { proc -> limit.isEmpty() || proc.day in limit }
                .map { it to it.execute() }
                .forEach { (proc, result) ->
                    print("Day ${proc.day} part 1 | ")
                    print(result.part1.first)
                    result.test1Result?.let {
                        print(" (TEST FAILED)")
                        if (!opaqueResults) {
                            print(": ${result.test1Result}")
                        }
                    }
                    println("")
                    print("Day ${proc.day} part 2 | ")
                    print(result.part2.first)
                    result.test2Result?.let {
                        print(" (TEST FAILED)")
                        if (!opaqueResults) {
                            print(": ${result.test2Result}")
                        }
                    }
                    println("")
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
