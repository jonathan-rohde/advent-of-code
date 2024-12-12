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
            it.size to it.sides(garden)
        }
        return costs.sumOf { it.first * it.second }.toLong()
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

private fun List<Pair<Int, Int>>.sides(garden: List<List<GardenCell>>): Int {
    val indicator = garden[first().second][first().first].indicator
    val list = filter {
        it.isPerimeter(garden)
    }.sortedWith(
        compareBy({ it.second }, { it.first })
    )
    val horizontalUp = list.groupBy { it.second }.values
        .flatMap { row ->  row.filter {
            cell ->
                val up = cell.second - 1
                up < 0 || garden[up][cell.first].indicator != indicator
            }
            .map { it.first }
            .chunkByIncrement( 1)
        }
        .filter { it.isNotEmpty() }
    val horizontalDown = list.groupBy { it.second }.values
        .flatMap { row ->  row.filter {
            cell ->
                val down = cell.second + 1
                down > garden.lastIndex || garden[down][cell.first].indicator != indicator
            }
            .map { it.first }
            .chunkByIncrement( 1)
        }
        .filter { it.isNotEmpty() }
    val verticalLeft = list.groupBy { it.first }.values
        .flatMap { row ->  row.filter {
                cell ->
            val left = cell.first - 1
            left < 0 ||garden[cell.second][left].indicator != indicator
        }
            .map { it.second }
            .sorted()
            .chunkByIncrement( 1)
        }
        .filter { it.isNotEmpty() }
    val verticalRight = list.groupBy { it.first }.values
        .flatMap { row ->  row.filter {
                cell ->
            val right = cell.first + 1
            right > garden[0].lastIndex  || garden[cell.second][right].indicator != indicator
        }
            .map { it.second }
            .sorted()
            .chunkByIncrement( 1)
        }
        .filter { it.isNotEmpty() }

    return horizontalUp.count() + horizontalDown.count() + verticalLeft.count() + verticalRight.count()
}

private fun List<Int>.chunkByIncrement(increment: Int): List<List<Int>> {
    if (isEmpty()) {
        return emptyList()
    }
    val result = mutableListOf<List<Int>>()
    var current = mutableListOf(this.first())
    for (i in 1 .. lastIndex) {
        if (this[i] - this[i - 1] == increment) {
            current.add(this[i])
        } else {
            result.add(current.toList())
            current = mutableListOf(this[i])
        }
    }
    result.add(current.toList())
    return result
}

private fun Pair<Int, Int>.isPerimeter(garden: List<List<GardenCell>>): Boolean {
    val (x, y) = this
    val indicator = garden[y][x].indicator
    return setOf(
        Pair(x - 1, y),
        Pair(x + 1, y),
        Pair(x, y - 1),
        Pair(x, y + 1)
    ).count { index ->
        val (x1, y1) = index
        !(y1 in garden.indices && x1 in garden[y1].indices) ||
                garden[y1][x1].indicator != indicator
    } > 0
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
