package aoc.years.y2023

import aoc.common.RunnerService
import aoc.common.Year

class Year2023 : Year {
    override fun execute(days: List<Int>, opaqueResults: Boolean, runs: Int) {
        val runner = RunnerService(
            days = listOf(
                // Add your days here
                Day01(),
                Day02(),
                Day03(),
                Day04(),
                Day05(),
                Day06(),
                Day07(),
                Day08(),
            ),
            year = 2024,
            opaqueResults = opaqueResults,
            executeRuns = runs
        )
        runner.execute(limit = days)
    }
}

fun main() {
    Year2023().execute(emptyList(), false, 1)
}
