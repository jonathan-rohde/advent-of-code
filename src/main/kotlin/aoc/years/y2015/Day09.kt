package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

fun main() {
    Day09().execute().printResults()
}

private val testInput = """
    London to Dublin = 464
    London to Belfast = 518
    Dublin to Belfast = 141
""".trimIndent()

class Day09 : Day(
    year = 2015,
    day = 9,
    part1 = Part(test = 605L, testInput = testInput),
    part2 = Part(test = 982L, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        val routes = input.map { it.parseRoute() }
        val requiredLocations = routes.flatMap { listOf(it.first, it.second) }.toSet()

        return requiredLocations.minOf {
            routes.shortest(it, requiredLocations)
        }
    }

    override fun part2(input: List<String>): Any {
        val routes = input.map { it.parseRoute() }
        val requiredLocations = routes.flatMap { listOf(it.first, it.second) }.toSet()

        return requiredLocations.maxOf {
            routes.longest(it, requiredLocations)
        }
    }
}

private typealias CityEdge = Triple<String, String, Int>

private fun String.parseRoute(): CityEdge {
    val cost = takeLastWhile { it.isDigit() }.toInt()
    val from = takeWhile { !it.isWhitespace() }
    val to = dropLast(length - lastIndexOf("=")).trim().takeLastWhile { !it.isWhitespace() }

    return Triple(from, to, cost)
}

private fun List<CityEdge>.shortest(
    start: String,
    required: Set<String>,
    cache: MutableMap<Pair<String, List<String>>, Long> = mutableMapOf(),
    visits: List<String> = emptyList()
): Long {
    val nVisit = visits + start
    return cache.getOrPut(start to nVisit) {
        val possibleMoves = filter { (it.first == start && it.second !in visits) || (it.second == start && it.first !in visits) }
            .ifEmpty {
                return@getOrPut 0L
            }
        possibleMoves.minOf {
            val nextStart = if (it.first == start) it.second else it.first
            it.third + shortest(nextStart, required, cache, nVisit)
        }
    }
}

private fun List<CityEdge>.longest(
    start: String,
    required: Set<String>,
    cache: MutableMap<Pair<String, List<String>>, Long> = mutableMapOf(),
    visits: List<String> = emptyList()
): Long {
    val nVisit = visits + start
    return cache.getOrPut(start to nVisit) {
        val possibleMoves = filter { (it.first == start && it.second !in visits) || (it.second == start && it.first !in visits) }
            .ifEmpty {
                return@getOrPut 0
            }
        possibleMoves.maxOf {
            val nextStart = if (it.first == start) it.second else it.first
            it.third + longest(nextStart, required, cache, nVisit)
        }
    }
}
