package aoc.years.y2025

import aoc.common.RunnerService
import aoc.common.Year

class Year2025 : Year {
    override fun execute(days: List<Int>, opaqueResults: Boolean) {
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
                Day09(),
//                Day10(),
                Day11(),
//                Day12()
                ),
            year = 2025,
            opaqueResults = opaqueResults
        )
        runner.execute(limit = days)
    }
}
