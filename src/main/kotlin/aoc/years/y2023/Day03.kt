package aoc.years.y2023

import aoc.common.Day
import aoc.common.printResults

fun main() {
    Day03().execute().printResults()
}

class Day03 : Day(2023, 3, 4361 to 467835) {
    override fun part1(input: List<String>): Any {
        val (parts, gears) = input.parseGrid()
        return parts.filter {
            val (x, y, value) = it
            val partLength = value.toString().length
            gears.any { (gx, gy, _) ->
                gx == x - 1 && gy == y ||
                gx == x + partLength && gy == y ||
                        gx >= x - 1 && gx <= x + partLength && gy == y - 1 ||
                        gx >= x - 1 && gx <= x + partLength && gy == y + 1
            }
        }.sumOf { it.third }
    }

    override fun part2(input: List<String>): Any {
        val (parts, gears) = input.parseGrid()
        return gears
            .filter { it.third == '*' }
            .mapNotNull {
                val (x, y, _) = it
                val surrounding = parts.filter {
                    val (px, py, value) = it
                    val partLength = value.toString().length
                    px == x + 1 && py == y ||
                            px == x - partLength && py == y ||
                            px >= x - partLength && px <= x + 1 && py == y - 1 ||
                            px >= x - partLength && px <= x + 1 && py == y + 1
                }
                if (surrounding.size == 2) {
                    val (part1, part2) = surrounding
                    part1.third * part2.third
                } else {
                    null
            }
        }.sum()
    }
}

private fun List<String>.parseGrid(): Pair<List<EnginePart>, List<Gear>> {
    val numberPattern = "\\d+".toRegex()
    val symbolPattern = "[^0-9.]".toRegex()

    val numberResult: MutableList<EnginePart> = mutableListOf()
    val symbolResult: MutableList<Gear> = mutableListOf()

    forEachIndexed { y, row ->
        val numbers = numberPattern.findAll(row).map { EnginePart(it.range.first, y, it.value.toInt()) }.toList()
        val symbols = symbolPattern.findAll(row).map { Gear(it.range.first, y, it.value.first()) }.toList()
        numberResult.addAll(numbers)
        symbolResult.addAll(symbols)
    }

    return numberResult to symbolResult
}

private typealias EnginePart = Triple<Int, Int, Int>
private typealias Gear = Triple<Int, Int, Char>
