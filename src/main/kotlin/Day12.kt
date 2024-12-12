import utils.readInput
import utils.testAndPrint
import kotlin.math.cos

fun main() {
    fun part1(input: List<String>): Long {
        val garden = input.parseGarden()
        val areas = mutableSetOf<List<Pair<Int, Int>>>()
        garden.forEachIndexed { y, row ->
            row.forEachIndexed { x, cell ->
                if (cell.consumed.not()) {
                    areas.add(garden.findArea(x, y).toList())
                }
            }
        }
        val costs = areas.map {
            it.size to it.perimeter(garden)
        }
        return costs.sumOf { it.first * it.second }.toLong()
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    val testInput = readInput("Day12_test")
    part1(testInput).testAndPrint(1930L)
    part2(testInput).testAndPrint(1206L)

    val input = readInput("Day12")
    part1(input).testAndPrint()
    part2(input).testAndPrint()
}

private fun List<String>.parseGarden(): List<List<GardenCell>> {
    return map { row ->
        row.split("").filter { cell -> cell.isNotEmpty() }
            .map { cell -> GardenCell(cell) }
    }
}

private fun List<List<GardenCell>>.findArea(x: Int, y: Int): Set<Pair<Int, Int>> {
    val cell = this[y][x]
    cell.consumed = true
    val area = setOf(
        Pair(x - 1, y),
        Pair(x + 1, y),
        Pair(x, y - 1),
        Pair(x, y + 1)
    ).asSequence().filter {
        it.second in indices && it.first in this[it.second].indices
    }.filter {
        this[it.second][it.first].indicator == cell.indicator
    }.filter { this[it.second][it.first].consumed.not() }.flatMap {
        findArea(it.first, it.second)
    }.toSet()
    return area + Pair(x, y)
}

private fun List<Pair<Int, Int>>.perimeter(garden: List<List<GardenCell>>): Int {
    return sumOf {
        val (x, y) = it
        val indicator = garden[y][x].indicator
        setOf(
            Pair(x - 1, y),
            Pair(x + 1, y),
            Pair(x, y - 1),
            Pair(x, y + 1)
        ).count { index ->
            val (x1, y1) = index
            !(y1 in garden.indices && x1 in garden[y1].indices) ||
            garden[y1][x1].indicator != indicator
        }
    }
}

data class GardenCell(
    val indicator: String,
    var consumed: Boolean = false
)
