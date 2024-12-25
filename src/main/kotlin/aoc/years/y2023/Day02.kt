package aoc.years.y2023

import aoc.common.Day
import aoc.common.printResults
import kotlin.math.max

fun main() {
    Day02().execute().printResults()
}

class Day02 : Day(2023, 2, 8 to 2286) {
    override fun part1(input: List<String>): Any {
        return input.map { parseGame(it) }
            .filter {
                it.isPossible(
                    mapOf(
                        Color.RED to 12,
                        Color.GREEN to 13,
                        Color.BLUE to 14
                    )
                )
            }.sumOf { it.id }
    }

    override fun part2(input: List<String>): Any {
        return input.map { parseGame(it) }
            .map { it.smallesSetOfCubes() }
            .sumOf { it.values.reduce(Int::times) }
    }

}

fun parseGame(input: String): Game {
    val parts = input.split(":")
    val id = parts[0].substring("Game".length).trim().toInt()
    return Game(
        id = id,
        draws = parseDraws(parts[1])
    )
}

fun parseDraws(input: String): List<Map<Color, Int>> {
    return input.split(";")
        .map(::parseDraw)
        .toList()
}

fun parseDraw(input: String): Map<Color, Int> =
    Color.entries
        .associateWith { it.extractNumberFromString(input) }

enum class Color(private val value: String) {
    RED("red"), BLUE("blue"), GREEN("green");

    fun extractNumberFromString(input: String): Int {
        val regex = "([0-9]+) $value".toRegex()
        val matches = regex.find(input)
        return matches?.groupValues?.get(1)?.toInt() ?: 0
    }

    fun smallestSet(left: Map<Color, Int>, right: Map<Color, Int>): Int {
        return max(left.getOrDefault(this, 0), right.getOrDefault(this, 0))
    }
}

data class Game(
    val id: Int,
    val draws: List<Map<Color, Int>>
) {
    fun isPossible(cubes: Map<Color, Int>): Boolean {
        return cubes.all {
            draws
                .map { draw -> draw[it.key] }
                .all { count -> (count ?: 0) <= it.value }
        }
    }

    fun smallesSetOfCubes(): Map<Color, Int> {
        return draws
            .reduce { left, right ->
                Color.entries
                    .associateWith { it.smallestSet(left, right) }
            }
    }
}
