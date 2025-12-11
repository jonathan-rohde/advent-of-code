package aoc.years.y2025

import aoc.common.Day
import aoc.common.printResults

class Day11 : Day(year = 2025, day = 11, test = 5L to 2L, testFile2 = "Day11_test2") {
    override fun part1(input: List<String>): Long {
        return input.toGraph().countPaths(from = "you", to = "out")
    }

    override fun part2(input: List<String>): Long {
        return input.toGraph().countPaths(from = "svr", to = "out", detours = setOf("dac", "fft"))
    }
}

fun main() {
    Day11().execute().printResults()
}

private fun Map<String, List<String>>.countPaths(
    from: String,
    to: String,
    cache: MutableMap<Pair<String, Set<String>>, Long> = mutableMapOf(),
    detours: Set<String> = emptySet(),
    visits: Set<String> = emptySet()
): Long = cache.getOrPut(Pair(from, visits)) {
    if (from == to) {
        if (visits.containsAll(detours)) 1 else 0
    } else {
        getValue(from).sumOf { neighbor ->
            val newVisits = if (from in detours) visits + from else visits
            countPaths(neighbor, to, cache, detours, newVisits)
        }
    }
}

private fun List<String>.toGraph(): Map<String, List<String>> {
    return associate { line ->
        line.split(": ").let { (from, to) ->
            from to to.split(" ")
        }
    }
}
