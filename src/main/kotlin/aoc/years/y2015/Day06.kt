package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import java.security.MessageDigest
import kotlin.math.abs
import kotlin.math.min
import kotlin.text.fold

fun main() {
    Day06().execute().printResults()
}

class Day06 : Day(
    year = 2015,
    day = 6,
    part1 = Part(test = null, testInput = "testInput"),
    part2 = Part(test = null, testInput = "testInput2"),
) {
    override fun part1(input: List<String>): Any {
        val grid = List(1000) { MutableList(1000) { false } }
        input.forEach {
            grid.performOperation(it)
        }

        return grid.sumOf { it.count { light -> light } }
    }

    override fun part2(input: List<String>): Any {
        val grid = List(1000) { MutableList(1000) { 0L } }
        input.forEach {
            grid.performOperation(it)
        }

        return grid.sumOf { it.sumOf { light -> light } }
    }
}

private typealias LightPosition = Pair<Int, Int>
@JvmName("toggle1")
private fun List<MutableList<Boolean>>.toggle(from: LightPosition, to: LightPosition) {
    (from.second .. to.second).forEach { y ->
        (from.first .. to.first).forEach { x ->
            this[y][x] = !this[y][x]
        }
    }
}
@JvmName("on1")
private fun List<MutableList<Boolean>>.on(from: LightPosition, to: LightPosition) {
    (from.second .. to.second).forEach { y ->
        (from.first .. to.first).forEach { x ->
            this[y][x] = true
        }
    }
}
@JvmName("off1")
private fun List<MutableList<Boolean>>.off(from: LightPosition, to: LightPosition) {
    (from.second .. to.second).forEach { y ->
        (from.first .. to.first).forEach { x ->
            this[y][x] = false
        }
    }
}

val regexCoords = "([0-9]+),([0-9]+) through ([0-9]+),([0-9]+)".toRegex()
@JvmName("performOperation1")
private fun List<MutableList<Boolean>>.performOperation(op: String) {
    val (command, coords) = if (op.startsWith("turn off")) {
        this::off to op.removePrefix("turn off ")
    } else if (op.startsWith("turn on")) {
        this::on to op.removePrefix("turn on ")
    } else if (op.startsWith("toggle")) {
        this::toggle to op.removePrefix("toggle ")
    } else throw IllegalArgumentException("Unrecognised op: $op")

    val match = regexCoords.findAll(coords).first()
    val from = match.groupValues[1].toInt() to match.groupValues[2].toInt()
    val to = match.groupValues[3].toInt() to match.groupValues[4].toInt()

    command(from, to)
}


@JvmName("toggle2")
private fun List<MutableList<Long>>.toggle(from: LightPosition, to: LightPosition) {
    (from.second .. to.second).forEach { y ->
        (from.first .. to.first).forEach { x ->
            this[y][x] += 2
        }
    }
}
@JvmName("on2")
private fun List<MutableList<Long>>.on(from: LightPosition, to: LightPosition) {
    (from.second .. to.second).forEach { y ->
        (from.first .. to.first).forEach { x ->
            this[y][x] += 1
        }
    }
}
@JvmName("off2")
private fun List<MutableList<Long>>.off(from: LightPosition, to: LightPosition) {
    (from.second .. to.second).forEach { y ->
        (from.first .. to.first).forEach { x ->
            this[y][x] -= 1
            if (this[y][x] < 0) {
                this[y][x] = 0
            }
        }
    }
}

@JvmName("performOperation2")
private fun List<MutableList<Long>>.performOperation(op: String) {
    val (command, coords) = if (op.startsWith("turn off")) {
        this::off to op.removePrefix("turn off ")
    } else if (op.startsWith("turn on")) {
        this::on to op.removePrefix("turn on ")
    } else if (op.startsWith("toggle")) {
        this::toggle to op.removePrefix("toggle ")
    } else throw IllegalArgumentException("Unrecognised op: $op")

    val match = regexCoords.findAll(coords).first()
    val from = match.groupValues[1].toInt() to match.groupValues[2].toInt()
    val to = match.groupValues[3].toInt() to match.groupValues[4].toInt()

    command(from, to)
}