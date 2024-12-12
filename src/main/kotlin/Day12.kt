import utils.println
import utils.readInput
import utils.testAndPrint
import kotlin.math.cos
import kotlin.time.measureTime

fun main() {
    fun part1(input: List<String>): Long {
        return input.calculateCost { list, garden ->
            list.perimeter(garden)
        }.toLong()
    }

    fun part2(input: List<String>): Long {
        return input.calculateCost {
            list, garden -> list.sides(garden)
        }.toLong()
    }

    val testInput = readInput("Day12_test")
    measureTime { part1(testInput).testAndPrint(1930L) }.println()
    measureTime { part2(testInput).testAndPrint(1206L) }.println()

    val input = readInput("Day12")
    measureTime { part1(input).testAndPrint() }.println()
    measureTime { part2(input).testAndPrint() }.println()
}

private fun List<String>.calculateCost(constFunc: (cell: List<Pair<Int, Int>>, garden: List<List<GardenCell>>) -> Int): Int {
    val garden = parseGarden()
    val areas = mutableSetOf<List<Pair<Int, Int>>>()
    garden.forEachIndexed { y, row ->
        row.forEachIndexed { x, cell ->
            if (cell.consumed.not()) {
                areas.add(garden.findArea(x, y).toList())
            }
        }
    }
    val costs = areas.map {
        it.size to constFunc(it, garden)
    }
    return costs.sumOf { it.first * it.second }
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
    val horizontalUp = list.countSides(1, indicator, garden) { a, b ->
        garden[a][b].indicator
    }
    val horizontalDown = list.countSides(-1, indicator, garden) {
            a, b -> garden[a][b].indicator
    }
    val verticalLeft = list.map { Pair(it.second, it.first) }.countSides(1, indicator, garden) {
            a, b -> garden[b][a].indicator
    }
    val verticalRight = list.map { Pair(it.second, it.first) }.countSides(-1, indicator, garden) {
            a, b -> garden[b][a].indicator
    }

    return horizontalUp + horizontalDown + verticalLeft + verticalRight
}

private fun List<Pair<Int, Int>>.countSides(direction: Int, indicator: String, garden: List<List<GardenCell>>, neighbourValue: (a: Int, b: Int) -> String ): Int {
    return groupBy { it.second }.values
        .flatMap { row ->  row.filter {
                cell ->
            val neighbour = cell.second - direction
            neighbour < 0 || neighbour >= garden.size || neighbourValue(neighbour, cell.first) != indicator
        }
            .map { it.first }
            .chunkByIncrement( 1)
        }
        .count { it.isNotEmpty() }
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

private fun Pair<Int, Int>.perimeter(garden: List<List<GardenCell>>): List<Pair<Int, Int>> {
    val (x, y) = this
    val indicator = garden[y][x].indicator
    return setOf(
        Pair(x - 1, y),
        Pair(x + 1, y),
        Pair(x, y - 1),
        Pair(x, y + 1)
    ).filter { index ->
        val (x1, y1) = index
        !(y1 in garden.indices && x1 in garden[y1].indices) ||
                garden[y1][x1].indicator != indicator
    }
}

private fun Pair<Int, Int>.isPerimeter(garden: List<List<GardenCell>>): Boolean {
    return perimeter(garden).isNotEmpty()
}

private fun List<Pair<Int, Int>>.perimeter(garden: List<List<GardenCell>>): Int {
    return sumOf {
        it.perimeter(garden).size
    }
}

data class GardenCell(
    val indicator: String,
    var consumed: Boolean = false
)
