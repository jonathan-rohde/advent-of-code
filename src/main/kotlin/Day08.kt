import utils.println
import utils.readInput

fun main() {
    fun part1(input: List<String>): Int {
        val map = input.parseMap()
        map.fillAntinodes()
//        printMap(map)
        return map.sumOf { row ->
            row.count {
                it.antinodes.isNotEmpty()
            }
        }
    }

    fun part2(input: List<String>): Int {
        val map = input.parseMap()
        map.fillAntinodes(checkDistance = false)
        printMap(map)
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
//    val test2 = part2(testInput)
//    test2.println()
//    check(test2 == 34)

    // Read the input from the `src/DayXX.txt` file.
    val input = readInput("Day08")
    val part1 = part1(input)
    part1.println()
//    val part2 = part2(input)
//    part2.println()
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

private fun printMap(map: List<List<Coord>>) {
    map.forEach { row ->
        row.forEach { coord ->
            if (coord.antinodes.isNotEmpty()) {
                print("#")
            } else if (coord.antenna != null) {
                print(coord.antenna)
            } else {
                print(".")
            }
//            if (coord.antenna != null) {
//                print(coord.antenna)
//            } else if (coord.antinodes.isNotEmpty()) {
//                print("#")
//            } else {
//                print(".")
//            }
        }
        println()
    }
}

private fun List<List<Coord>>.fillAntinodes(checkDistance: Boolean = true) {
    this.forEachIndexed { y, row ->
        row.forEachIndexed { x, coord ->
            if (coord.antenna != null) {
                val possiblePair: List<Pair<Int, Int>> = findPossiblePair(x, y, coord.antenna)
                possiblePair.forEach { pair ->
                    val distanceX = x - pair.first
                    val distanceY = y - pair.second
                    val locations = calculate8(
                        x,
                        y,
                        pair.first,
                        pair.second,
                        distanceX,
                        distanceY
                    )
                    locations.filter { (lx, ly) ->
                        ly in indices && lx in row.indices
                    }.forEach {
                        val (lx, ly) = it
                        if (this[ly][lx].antenna != coord.antenna && !this[ly][lx].antinodes.contains(coord.antenna)) {
                            this[ly][lx].antinodes.add(coord.antenna)
                        }
                    }
                }

            }
        }
    }
}

private fun calculate8(
    x: Int, y: Int, x2: Int, y2: Int, distanceX: Int, distanceY: Int
): List<Pair<Int, Int>> {
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
    val antenna: Char?,
    var antinodes: MutableSet<Char> = mutableSetOf()
)
