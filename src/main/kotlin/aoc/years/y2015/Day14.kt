package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

fun main() {
    Day14().execute().printResults()
}

private val testInput = """
    Comet can fly 14 km/s for 10 seconds, but then must rest for 127 seconds.
    Dancer can fly 16 km/s for 11 seconds, but then must rest for 162 seconds.
""".trimIndent()

class Day14 : Day(
    year = 2015,
    day = 14,
    part1 = Part(test = 1120L, testInput = testInput, testLimit = 1000, limit = 2503),
    part2 = Part(test = 689, testInput = testInput, testLimit = 1000, limit = 2503),
) {

    override fun part1(input: List<String>, limit: Int): Any {
        return input.map { it.toReindeer() }.maxOf { it.distanceAfter(limit) }
    }

    override fun part2(input: List<String>, limit: Int): Any {
        val reindeers = input.map { it.toReindeer() }
        val scores = reindeers.associateWith { 0 }.toMutableMap()
        (1..limit).forEach { i ->
            val standing = reindeers.map { it to it.distanceAfter(i) }.sortedByDescending { it.second }
            val winningScore = standing.first().second
            val winners = standing.takeWhile { it.second == winningScore }
            winners.forEach { winner ->
                scores[winner.first] = scores[winner.first]!! + 1
            }
        }

        return scores.maxOf { it.value }
    }
}

private data class Reindeer(val name: String, val speed: Int, val duration: Int, val pause: Int) {
    fun distanceAfter(seconds: Int): Long {
        val segment = duration + pause
        val distancePerSegment = duration * speed
        val amountSegments = seconds / segment
        val remain = seconds % segment
        val portion = if (duration <= remain) duration * speed else remain * speed
        return amountSegments.toLong() * distancePerSegment + portion
    }
}

private val reindeerRegex = "([a-zA-Z]+) can fly ([0-9]+) km/s for ([0-9]+) seconds, but then must rest for ([0-9]+) seconds".toRegex()
private fun String.toReindeer(): Reindeer {
    val (name, speed, duration, pause) = reindeerRegex.find(this)!!.destructured
    return Reindeer(name, speed.toInt(), duration.toInt(), pause.toInt())
}
