package aoc.years.y2016

import aoc.common.Day
import aoc.common.Part
import aoc.common.downloader.inputDataProvider
import aoc.common.printResults

fun main() {
    Day08().execute().printResults()
}

private val testInput = """
    rect 3x2
    rotate column x=1 by 1
    rotate row y=0 by 4
    rotate column x=1 by 1
""".trimIndent()

private val testInputReal = inputDataProvider.getInputData(2016, 8).joinToString("\n")


class Day08 : Day(
    year = 2016,
    day = 8,
    part1 = Part(test = 2, testInput = testInput),
    part2 = Part(test = null, testInput = ""),
) {
    override fun part1(input: List<String>): Any {
        return input.getLights().sumOf { row -> row.count { it } }
    }

    override fun part2(input: List<String>): Any {
        val lights = input.getLights()
//        lights.show()
        val signs = lights.cutInChars()
        return signs.decipher()
    }
}

private fun List<String>.getLights(): List<List<Boolean>> {
    val lights = List(6) {
        MutableList(50) { false }
    }
    forEach { action ->
        lights.apply(action.parseAction())
    }
    return lights
}

private fun List<List<Boolean>>.show() {
    forEach { row ->
        row.forEach { col ->
            if (col) {
                print("#")
            } else {
                print(".")
            }
        }
        println()
    }
    println()
    println()
}

private interface LightAction
private data class TurnOn(val width: Int, val height: Int): LightAction
private enum class RotateDirection { ROW, COLUMN }
private data class Rotate(val direction: RotateDirection, val index: Int, val distance: Int): LightAction

private fun String.parseAction(): LightAction {
    return when (takeWhile { !it.isWhitespace() }) {
        "rect" -> {
            val (m, n) = getMxN()
            TurnOn(m, n)
        }
        "rotate" -> getRotation()
        else -> TODO()
    }
}

private val mXnRegex = "(\\d+)x(\\d+)".toRegex()
private fun String.getMxN(): Pair<Int, Int> {
    val match = mXnRegex.find(this)
    return match!!.groupValues[1].toInt() to match.groupValues[2].toInt()
}

private fun String.getRotation(): Rotate {
    val direction = when {
        startsWith("rotate column") -> RotateDirection.COLUMN
        startsWith("rotate row") -> RotateDirection.ROW
        else -> TODO()
    }
    val index = dropWhile { !it.isDigit() }.takeWhile { it.isDigit() }.toInt()
    val distance = takeLastWhile { it.isDigit() }.toInt()

    return Rotate(direction, index, distance)
}


private fun List<MutableList<Boolean>>.apply(action: LightAction) {
    when (action) {
        is TurnOn -> apply(action)
        is Rotate -> when (action.direction) {
            RotateDirection.ROW -> applyRow(action)
            RotateDirection.COLUMN -> applyCol(action)
        }
    }
}
private fun List<MutableList<Boolean>>.apply(action: TurnOn) {
    (0 until action.height).forEach { row ->
        (0 until action.width).forEach { col ->
            this[row][col] = true
        }
    }
}

private fun List<MutableList<Boolean>>.applyRow(action: Rotate) {
    val row = this[action.index].toMutableList()
        .let {
            it.takeLast(action.distance) + it.dropLast(action.distance)
        }
    row.forEachIndexed { index, light ->
        this[action.index][index] = light
    }
}

private fun List<MutableList<Boolean>>.applyCol(action: Rotate) {
    val column = getColumn(action.index).let {
        it.takeLast(action.distance) + it.dropLast(action.distance)
    }
    column.forEachIndexed { index, light ->
        this[index][action.index] = light
    }
}

private fun List<MutableList<Boolean>>.getColumn(x: Int): List<Boolean> {
    return map { it[x] }
}

private fun List<List<Boolean>>.cutInChars(): List<List<List<Boolean>>> {
    val result = mutableListOf<List<List<Boolean>>>()
    (0..this[0].lastIndex - 5 step 5).forEach { start ->
        val sign = List(6) { MutableList(5) { false } }
        (0 until 5).forEach { col ->
            (0 until 6).forEach { row ->
                sign[row][col] = this[row][start + col]
            }
        }
        result.add(sign)
    }
    return result
}

private fun List<List<List<Boolean>>>.decipher(): String {
    forEach { it.show() }
    return joinToString("") { sign ->
        alphabet.entries.firstOrNull { it.value == sign }?.key ?: "?"
    }
}

private val alphabet: Map<String, List<List<Boolean>>> = mapOf(
    "A" to listOf(
        listOf(false, true, true, false, false),
        listOf(true, false, false, true, false),
        listOf(true, false, false, true, false),
        listOf(true, true, true, true, false),
        listOf(true, false, false, true, false),
        listOf(true, false, false, true, false)
    ),
    "E" to listOf(
        listOf(true, true, true, true, false),
        listOf(true, false, false, false, false),
        listOf(true, true, true, false, false),
        listOf(true, false, false, false, false),
        listOf(true, false, false, false, false),
        listOf(true, true, true, true, false)
    ),
    "O" to listOf(
        listOf(false, true, true, false, false),
        listOf(true, false, false, true, false),
        listOf(true, false, false, true, false),
        listOf(true, false, false, true, false),
        listOf(true, false, false, true, false),
        listOf(false, true, true, false, false)
    ),
    "R" to listOf(
        listOf(true, true, true, false, false),
        listOf(true, false, false, true, false),
        listOf(true, false, false, true, false),
        listOf(true, true, true, false, false),
        listOf(true, false, true, false, false),
        listOf(true, false, false, true, false)
    ),
    "G" to listOf(
        listOf(false, true, true, false, false),
        listOf(true, false, false, true, false),
        listOf(true, false, false, false, false),
        listOf(true, false, true, true, false),
        listOf(true, false, false, true, false),
        listOf(false, true, true, true, false)
    ),
    "P" to listOf(
        listOf(true, true, true, false, false),
        listOf(true, false, false, true, false),
        listOf(true, false, false, true, false),
        listOf(true, true, true, false, false),
        listOf(true, false, false, false, false),
        listOf(true, false, false, false, false)
    ),
    "H" to listOf(
        listOf(true, false, false, true, false),
        listOf(true, false, false, true, false),
        listOf(true, true, true, true, false),
        listOf(true, false, false, true, false),
        listOf(true, false, false, true, false),
        listOf(true, false, false, true, false),
    ),
    "Y" to listOf(
        listOf(true, false, false, false, true),
        listOf(true, false, false, false, true),
        listOf(false, true, false, true, false),
        listOf(false, false, true, false, false),
        listOf(false, false, true, false, false),
        listOf(false, false, true, false, false)
    )
)
