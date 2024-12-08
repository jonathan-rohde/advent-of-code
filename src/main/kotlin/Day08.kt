import utils.println
import utils.readInput
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    fun part1(input: List<String>): Int {
        val map = input.parseMap()
        map.fillAntinodes()
        return map.sumOf { row ->
            row.count {
                it.antinodes.isNotEmpty()
            }
        }
    }

    fun part2(input: List<String>): Int {
        val map = input.parseMap()
        map.fillAntinodes(checkDistance = false)
        return map.sumOf { row ->
            row.count {
                it.antinodes.isNotEmpty()
            }
        }
    }

    // Or read a large test input from the `src/DayXX_test.txt` file:
    val testInput = readInput("Day08_test")
    val test1 = part1(testInput)
    test1.println()
    check(test1 == 14)
    val test2 = part2(testInput)
    test2.println()
    check(test2 == 34)

    // Read the input from the `src/DayXX.txt` file.
    val input = readInput("Day08")
    val part1 = part1(input)
    part1.println()
    val part2 = part2(input)
    part2.println()
}

private fun List<String>.parseMap(): List<List<Coord>> {
    return this.map { line ->
        line.map { char ->
            when (char) {
                '.' -> Coord(null)
                else -> Coord(char)
            }
        }
    }
}

private fun List<List<Coord>>.fillAntinodes(checkDistance: Boolean = true) {
    this.forEachIndexed { y, row ->
        row.forEachIndexed { x, coord ->
            if (coord.antenna != null) {
                val possiblePair: List<Pair<Int, Int>> = findPossiblePair(x, y, coord.antenna!!)
                possiblePair.forEach { pair ->
                    val distanceX = x - pair.first
                    val distanceY = y - pair.second
                    val locations = calculate8(
                        x,
                        y,
                        pair.first,
                        pair.second,
                        distanceX,
                        distanceY,
                        checkDistance = checkDistance,
                        maxX = row.size - 1,
                        maxY = this.size - 1,
                    )
                    locations.filter { (lx, ly) ->
                        ly in indices && lx in row.indices
                    }.forEach {
                        val (lx, ly) = it
                        if ((
                                    this[ly][lx].antenna != coord.antenna
                                            || !checkDistance
                                ) && !this[ly][lx].antinodes.contains(coord.antenna)) {
                            this[ly][lx].antinodes.add(coord.antenna!!)
                        }
                    }
                }

            }
        }
    }
}

private fun calculate8(
    x: Int, y: Int, x2: Int, y2: Int, distanceX: Int, distanceY: Int,
    checkDistance: Boolean = true,
    maxX: Int = 100, maxY: Int = 100
): List<Pair<Int, Int>> {

    if (!checkDistance) {
        val pairs = mutableListOf<Pair<Int,Int>>()
        for (i in 0 until min(maxX, maxY)) {
            pairs.add(Pair(x + i*distanceX, y + i*distanceY))
            pairs.add(Pair(x2 - i*distanceX, y2 - i*distanceY))
        }
        return pairs
    }
    return listOf(
        Pair(x + distanceX, y + distanceY),
        Pair(x2 - distanceX, y2 - distanceY),
    )
}

private fun List<List<Coord>>.findPossiblePair(x: Int, y: Int, frequency: Char): List<Pair<Int, Int>> {
    return flatMapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, coord ->
            if (x != colIndex && y != rowIndex) {
                if (coord.antenna == frequency) {
                    Pair(colIndex, rowIndex)
                } else {
                    null
                }
            } else {
                null
            }
        }.filterNotNull()
    }
}

data class Coord(
    var antenna: Char?,
    var antinodes: MutableSet<Char> = mutableSetOf()
)
