package aoc.years.y2024

import aoc.common.RunnerService
import aoc.common.Year

class Year2024 : Year {
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
                Day10(),
                Day11(),
                Day12(),
                Day13(),
                Day14(),
                Day15(),
                Day16(),
                Day17(),
                Day18(),
                Day19(),
                Day20(),
                Day21(),
                Day22(),
                Day23(),
                Day24(),
                Day25()
                ),
            year = 2024,
            opaqueResults = opaqueResults
        )
        runner.execute(limit = days)
    }
}
