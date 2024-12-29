package aoc.years.y2023

import aoc.common.Day
import aoc.common.printResults
import utils.lcm

class Day08 : Day(year = 2023, day = 8, test = 2 to 6L, testFile2 = "Day08_p2_test") {
    override fun part1(input: List<String>): Any {
        val (movement, routes) = input.parseProblem()
        var current = "AAA"
        var steps = 0
        while (current != "ZZZ") {
            val next = movement.next()
            val (left, right) = routes[current]!!
            current = if (next == "R") right else left
            steps++
        }
        return steps
    }

    override fun part2(input: List<String>): Any {
        val (movement, routes) = input.parseProblem()
        val currents = routes.keys.filter { it.endsWith("A") }
            .map { loopLength(it, movement, routes) }
        return currents.lcm()
    }
}

fun main() {
    Day08().execute().printResults()
}

private fun List<String>.parseProblem(): Pair<Movement, Routes> {
    val movement = Movement(this[0])
    val routes = this.drop(2).map {
        val (from, to) = it.split(" = ")
        val (left, right) = "\\(([A-Z0-9]+), ([A-Z0-9]+)\\)".toRegex().find(to)!!.destructured
        from to (left to right)
    }.toMap()
    return movement to routes
}

private typealias Routes = Map<String, Pair<String, String>>

private data class Movement(
    val direction: String,
    var pos: Int = 0
)

private fun Movement.next(): String {
    return direction[pos].toString().also {
        pos = (pos + 1) % direction.length
    }
}

private fun loopLength(start: String, movement: Movement, routes: Routes): Long {
    val movementCopy = movement.copy()
    var current = start
    var steps = 0L
    do {
        val next = movementCopy.next()
        val (left, right) = routes[current]!!
        current = if (next == "R") right else left
        steps++
    }
    while (!current.endsWith("Z"))
    return steps
}
