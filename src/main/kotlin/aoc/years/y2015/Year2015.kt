package aoc.years.y2015

import aoc.common.RunnerService
import aoc.common.Year
import aoc.years.y2025.Day01
import aoc.years.y2025.Day02
import aoc.years.y2025.Day03
import aoc.years.y2025.Day04
import aoc.years.y2025.Day05
import aoc.years.y2025.Day06
import aoc.years.y2025.Day07
import aoc.years.y2025.Day08
import aoc.years.y2025.Day09
import aoc.years.y2025.Day10
import aoc.years.y2025.Day11
import aoc.years.y2025.Day12

class Year2015 : Year {
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
            year = 2025,
            opaqueResults = opaqueResults
        )
        runner.execute(limit = days)
    }
}
