package aoc.years.y2023

import aoc.common.Day
import aoc.common.printResults
import utils.toLongList

class Day06 : Day(year = 2023, day = 6, test = 288L to 71503L) {
    override fun part1(input: List<String>): Any {
        val races = input.parseRace()
        return races.map { it.calculateWaysOfWinning() }
            .reduce(Long::times)
    }

    override fun part2(input: List<String>): Any {
        val races = input.parseRace()
        val actualRace = Race(
            first = races.joinToString("") { it.first.toString() }.toLong(),
            second = races.joinToString("") { it.second.toString() }.toLong()
        )
        return actualRace.calculateWaysOfWinning()
    }

}

fun main() {
    Day06().execute().printResults()
}

private fun List<String>.parseRace(): List<Race> {
    val times = this[0].substring("Time:".length).toLongList()
    val distances = this[1].substring("Distance:".length).toLongList()

    require(times.size == distances.size) { "Times and distances must be the same size" }

    return times.zip(distances)
}

private fun Race.calculateWaysOfWinning(): Long {
    var ways = 0L
    (0 .. first).forEach { speed ->
        val remainingTime = first - speed
        val distance = speed * remainingTime
        if (distance > second) {
            ways++
        }
    }
    return ways
}

private typealias Race = Pair<Long, Long>
