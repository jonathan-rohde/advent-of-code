package aoc.years.y2025

import aoc.common.Day
import aoc.common.printResults

class Day11 : Day(year = 2025, day = 11, test = 5L to 2L, testFile2 = "Day11_test2") {
    override fun part1(input: List<String>): Long {
        return input.toGraph().countPaths("you", "out")
    }

    override fun part2(input: List<String>): Long {
        return input.toGraph().countPathsWithDetour("svr", "out")
    }
}

fun main() {
    Day11().execute().printResults()
}

private fun Map<String, List<String>>.countPaths(
    from: String,
    to: String
): Long {
    return if (from == to) 1
    else getValue(from).sumOf { neighbor -> countPaths(neighbor, to) }
}

private fun Map<String, List<String>>.countPathsWithDetour(
    from: String,
    to: String,
    cache: MutableMap<Triple<String, Boolean, Boolean>, Long> = mutableMapOf(),
    foundDAC: Boolean = false,
    foundFFT: Boolean = false,
): Long {
    return cache.getOrPut(Triple(from, foundDAC, foundFFT)) {
        if (from == to) {
            return@getOrPut if (from.hasAllDetours(foundDAC, foundFFT)) 1 else 0
        }
        getValue(from).sumOf { neighbor ->
            countPathsWithDetour(
                from = neighbor,
                to = to,
                cache = cache,
                foundDAC = neighbor.hasDetour("dac", foundDAC),
                foundFFT = neighbor.hasDetour("fft", foundFFT)
            )
        }
    }

}

private fun String.hasDetour(detour: String, rec: Boolean): Boolean {
    return rec || this == detour
}

private fun String.hasAllDetours(
    foundDAC: Boolean,
    foundFFT: Boolean
): Boolean {
    return (foundDAC || this == "dac") && (foundFFT || this == "fft")
}

private fun List<String>.toGraph(): Map<String, List<String>> {
    return associate {
        val (from, to) = it.split(": ")
        val neighbors = to.split(" ")
        from to neighbors
    }
}
