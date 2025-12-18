package aoc.years.y2023

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import kotlin.math.max
import kotlin.math.min

private val testInput = """
    .|...\....
    |.-.\.....
    .....|-...
    ........|.
    ..........
    .........\
    ..../.\\..
    .-.-/..|..
    .|....-|.\
    ..//.|....
""".trimIndent()

class Day16 : Day(
    year = 2023,
    day = 16,
    part1 = Part(test = 46, testInput = testInput),
    part2 = Part(test = 51, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        val maize = input.parseMaize()
        maize.energize()
        return maize.sumOf {
            it.count { cell -> cell.second > 0 }
        }
    }

    override fun part2(input: List<String>): Any {
        val rowIndices = input.indices
        val columnIndices = input[0].indices

        val a = rowIndices.flatMap {
            listOf(
                execute(input, 0, it, BeamDirection.RIGHT),
                execute(input, input[it].lastIndex, it, BeamDirection.LEFT),
            )
        }.max()
        val b = columnIndices.flatMap {
            listOf(
                execute(input, it, 0, BeamDirection.DOWN),
                execute(input, it, input.lastIndex, BeamDirection.UP),
            )
        }.max()
        return max(a, b)
    }

    private fun execute(input: List<String>, x: Int, y: Int, direction: BeamDirection): Int {
        val maize = input.parseMaize()
        maize.energize(mutableListOf(MaizeCoord(x, y, direction)))
        return maize.sumOf {
            it.count { cell -> cell.second > 0 }
        }
    }
}

fun main() {
    Day16().execute().printResults()
}

private typealias MaizeCoord = Triple<Int, Int, BeamDirection>
private typealias Cell = Pair<MaizeObject, Int>
private typealias Maize = List<MutableList<Cell>>

private enum class BeamDirection {
    UP {
        override fun hitObject(obj: MaizeObject): List<BeamDirection> {
            return when (obj) {
                MaizeObject.EMPTY -> listOf(UP)
                MaizeObject.MIRROR_UP -> listOf(RIGHT)
                MaizeObject.MIRROR_DOWN -> listOf(LEFT)
                MaizeObject.SPLITTER_HORIZONTAL -> listOf(LEFT, RIGHT)
                MaizeObject.SPLITTER_VERTICAL -> listOf(UP)
            }
        }

    }, DOWN {
        override fun hitObject(obj: MaizeObject): List<BeamDirection> {
            return when (obj) {
                MaizeObject.EMPTY -> listOf(DOWN)
                MaizeObject.MIRROR_UP -> listOf(LEFT)
                MaizeObject.MIRROR_DOWN -> listOf(RIGHT)
                MaizeObject.SPLITTER_HORIZONTAL -> listOf(LEFT, RIGHT)
                MaizeObject.SPLITTER_VERTICAL -> listOf(DOWN)
            }
        }
    }, LEFT {
        override fun hitObject(obj: MaizeObject): List<BeamDirection> {
            return when (obj) {
                MaizeObject.EMPTY -> listOf(LEFT)
                MaizeObject.MIRROR_UP -> listOf(DOWN)
                MaizeObject.MIRROR_DOWN -> listOf(UP)
                MaizeObject.SPLITTER_HORIZONTAL -> listOf(LEFT)
                MaizeObject.SPLITTER_VERTICAL -> listOf(UP, DOWN)
            }
        }
    }, RIGHT {
        override fun hitObject(obj: MaizeObject): List<BeamDirection> {
            return when (obj) {
                MaizeObject.EMPTY -> listOf(RIGHT)
                MaizeObject.MIRROR_UP -> listOf(UP)
                MaizeObject.MIRROR_DOWN -> listOf(DOWN)
                MaizeObject.SPLITTER_HORIZONTAL -> listOf(RIGHT)
                MaizeObject.SPLITTER_VERTICAL -> listOf(UP, DOWN)
            }
        }
    };

    abstract fun hitObject(obj: MaizeObject): List<BeamDirection>
}

private fun Maize.energize(current: MutableList<MaizeCoord> = mutableListOf(Triple(0, 0, BeamDirection.RIGHT))) {
    val visited = mutableSetOf<MaizeCoord>()
    var count = 0
    while (current.isNotEmpty()) {
        count++
        val coord = current.removeFirst()
        if (visited.contains(coord)) continue
        visited.add(coord)
        val (x, y, direction) = coord

        if (y !in indices || x !in this[y].indices) continue

        this[y][x] = this[y][x].copy(second = this[y][x].second + 1)
        next(x, y, direction).forEach(current::add)
    }
}

private fun Maize.next(x: Int, y: Int, direction: BeamDirection): List<MaizeCoord> {
    return direction.hitObject(this[y][x].first)
        .map { it.move(x, y) }
}

private fun BeamDirection.move(x: Int, y: Int): MaizeCoord {
    return when (this) {
        BeamDirection.UP -> MaizeCoord(x, y - 1, BeamDirection.UP)
        BeamDirection.DOWN -> MaizeCoord(x, y + 1, BeamDirection.DOWN)
        BeamDirection.LEFT -> MaizeCoord(x - 1, y, BeamDirection.LEFT)
        BeamDirection.RIGHT -> MaizeCoord(x + 1, y, BeamDirection.RIGHT)
    }
}

private enum class MaizeObject {
    EMPTY,
    MIRROR_UP,
    MIRROR_DOWN,
    SPLITTER_HORIZONTAL,
    SPLITTER_VERTICAL;

    companion object {
        fun fromString(string: String): MaizeObject {
            return when (string) {
                "." -> EMPTY
                "/" -> MIRROR_UP
                "\\" -> MIRROR_DOWN
                "-" -> SPLITTER_HORIZONTAL
                "|" -> SPLITTER_VERTICAL
                else -> throw IllegalArgumentException("Unknown MaizeObject: $string")
            }
        }

        fun fromChar(c: Char): MaizeObject {
            return fromString(c.toString())
        }
    }
}
private fun List<String>.parseMaize(): Maize {
    return map { row ->
        row.map { Pair(MaizeObject.fromChar(it), 0) }.toMutableList()
    }
}