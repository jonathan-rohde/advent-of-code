package aoc.years.y2023

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

private val testInput = """
    #.##..##.
    ..#.##.#.
    ##......#
    ##......#
    ..#.##.#.
    ..##..##.
    #.#.##.#.
    
    #...##..#
    #....#..#
    ..##..###
    #####.##.
    #####.##.
    ..##..###
    #....#..#
""".trimIndent()

class Day13 : Day(
    year = 2023,
    day = 13,
    part1 = Part(test = 405L, testInput = testInput),
    part2 = Part(test = 400L, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        return input.parsePatterns().sumOf { pattern ->
            pattern.getReflections()
        }
    }

    override fun part2(input: List<String>): Any {
        return input.parsePatterns().sumOf { pattern ->
            pattern.getReflections(smudge = 1)
        }
    }
}

fun main() {
    Day13().execute().printResults()
}

private fun Pattern.getReflections(smudge: Int = 0): Long {
    val vertical = this[0].indices.drop(0).dropLast(1).firstOrNull { x ->
        this.sumOf { row -> row.countMismatches(x) } == smudge
    }?.toLong()?.plus(1) ?: 0L
    val horizontal = (0 until size - 1).firstOrNull { y ->
        countMismatches(y) == smudge
    }?.toLong()?.plus(1) ?: 0L
    return vertical + (horizontal * 100L)
}

private typealias Pattern = List<String>
private fun List<String>.parsePatterns(): List<Pattern> {
    val patterns = mutableListOf<Pattern>()
    var currentPattern = mutableListOf<String>()
    forEach { line ->
        if (line.isBlank()) {
            patterns += currentPattern
            currentPattern = mutableListOf()
        } else {
            currentPattern += line
        }
    }
    patterns += currentPattern
    return patterns
}


private fun String.countMismatches(x: Int): Int {
    val left = substring(0, x + 1)
    val right = substring(x + 1).reversed()

    val maxLength = minOf(left.length, right.length)
    val checkLeft = left.takeLast(maxLength)
    val checkRight = right.takeLast(maxLength)

    return checkLeft.indices.count { checkLeft[it] != checkRight[it] }
}

private fun List<String>.countMismatches(y: Int): Int {
    val top = take(y + 1)
    val bottom = drop(y + 1).reversed()

    val maxLength = minOf(top.size, bottom.size)
    val checkTop = top.takeLast(maxLength)
    val checkBottom = bottom.takeLast(maxLength)

    return checkTop.indices.sumOf { i ->
        checkTop[i].indices.count { j ->
            checkTop[i][j] != checkBottom[i][j]
        }
    }
}
