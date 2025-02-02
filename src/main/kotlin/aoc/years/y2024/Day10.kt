package aoc.years.y2024

import aoc.common.Day
import aoc.common.printResults
import utils.readInput
import utils.testAndPrint

class Day10 : Day(2024, 10, 36L to 81L) {
    override fun part1(input: List<String>): Long {
        val map = input.toMap()
        val startingPositions = map.find(0)
        return map.evaluations(startingPositions)


    }

    override fun part2(input: List<String>): Long {
        val map = input.toMap()
        val startingPositions = map.find(0)
        return map.evaluations2(startingPositions)
    }
}

fun main() {
    Day10().execute().printResults()

}

private fun List<String>.toMap(): List<List<Int>> = map { it.map { c -> c.toString().toInt() }}

private fun List<List<Int>>.find(value: Int): List<Pair<Int, Int>> {
    val result = mutableListOf<Pair<Int, Int>>()
    for (y in indices) {
        for (x in this[y].indices) {
            if (this[y][x] == value) {
                result.add(Pair(x, y))
            }
        }
    }
    return result
}

private fun List<List<Int>>.evaluations(startingPositions: List<Pair<Int, Int>>): Long {
    var evaluations = 0
    for ((x, y) in startingPositions) {
        val trails = createPath(x, y)
            .map { path -> path[path.lastIndex] }
            .toSet()
        evaluations += trails.size
    }
    return evaluations.toLong()
}

private fun List<List<Int>>.evaluations2(startingPositions: List<Pair<Int, Int>>): Long {
    var evaluations = 0
    for ((x, y) in startingPositions) {
        val trails = createPath(x, y)
            .distinct()
        evaluations += trails.size
    }
    return evaluations.toLong()
}

private fun List<List<Int>>.createPath(x: Int, y: Int): List<List<Pair<Int, Int>>> {
    val value = this[y][x]
    if (value == 9) {
        return listOf(listOf(Pair(x, y)))
    }
    val nextPositions = nextPositions(x, y)
        .filter { (x1, y1) ->
            this[y1][x1] == value + 1
        }
    if (nextPositions.isEmpty()) {
        return emptyList()
    }
    val routesFromHere = nextPositions
        .flatMap {
            createPath(it.first, it.second)
                .map { path -> listOf(Pair(x, y)) + path }
        }
    return routesFromHere

}

private fun List<List<Int>>.nextPositions(x: Int, y: Int): List<Pair<Int, Int>> {
    return listOf(
        Pair(x - 1, y),
        Pair(x + 1, y),
        Pair(x, y - 1),
        Pair(x, y + 1),
    ).filter {
        it.second in indices && it.first in this[it.second].indices
    }
}
