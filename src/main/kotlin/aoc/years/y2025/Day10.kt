package aoc.years.y2025

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import utils.readInput
import utils.testAndPrint
import utils.toIntList

private val testInput = """
    [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
    [...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
    [.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}
""".trimIndent()

class Day10 : Day(
    year = 2025,
    day = 10,
    part1 = Part(test = 7L, testInput = testInput),
    part2 = Part(test = -1L, testInput = testInput),
) {
    override fun part1(input: List<String>): Long {
        return input.map { it.parseMachine() }
            .sumOf {
                it.minSteps()
            }
    }

    override fun part2(input: List<String>): Long {
        return -1L // not yet solved in kotlin
//        return input.map { it.parseMachine() }.asSequence()
//            .map { it.minStepsJoltage().also { println(it) } }
//            .sum()
//            .sumOf {
//                it.minStepsJoltage().also {
//                    println(it)
//                }
//            }
    }
}

fun main() {
    Day10().execute().printResults()

}

private fun Triple<List<Boolean>, List<List<Int>>, List<Int>>.minSteps(): Long {
    val (machine, buttons, _) = this
    val target = machine.map { false }
    return minSteps(target, machine, buttons)
}

private fun Triple<List<Boolean>, List<List<Int>>, List<Int>>.minStepsJoltage(): Long {
    val (_, buttons, joltages) = this
    val start = joltages.map { 0 }
    return minStepsJoltage(joltages, start, buttons)
}

private val cache = mutableMapOf<Pair<List<Boolean>, List<List<Int>>>, Long>()

private fun minSteps(current: List<Boolean>, target: List<Boolean>, buttons: List<List<Int>>, previousStates: Set<List<Boolean>> = emptySet()): Long {
    if (current.isMatch(target)) return 0L
    return cache.getOrPut(current to buttons) {
        buttons.minOf {
            val next = current.toMutableList()
            next.toggle(it)
            if (next in previousStates) {
                return@minOf Integer.MAX_VALUE.toLong()
            }
            val states = previousStates.toMutableSet()
            states.add(current)
            1 + minSteps(next, target, buttons, states)
        }
    }
}

private val cacheJoltage = mutableMapOf<Pair<List<Int>, List<List<Int>>>, Long>()
private fun minStepsJoltage(current: List<Int>, target: List<Int>, buttons: List<List<Int>>, previousStates: Set<List<Int>> = emptySet(), counter: Int = 0): Long {
    if (current.isMatch(target)) return 0L
    if (counter > 300) return Integer.MAX_VALUE.toLong()
    return cacheJoltage.getOrPut(current to buttons) {
        buttons.minOf {button ->
            val maxButtonPresses = current.filterIndexed { index, _ -> index in button }.minOrNull() ?: 1
            (maxButtonPresses downTo 0).minOf { presses ->
                val next = current.toMutableList()
                repeat(presses) {next.toggle(button)}
                if (next in previousStates || next.isNegative()) {
                    return@minOf Integer.MAX_VALUE.toLong()
                }
                val states = previousStates.toMutableSet()
                states.add(current)
                presses + minStepsJoltage(next, target, buttons, states, counter + presses)
            }
        }
    }
}

@JvmName("toggleBooleanList")
private fun MutableList<Boolean>.toggle(button: List<Int>) {
    for (index in button) {
        this[index] = !this[index]
    }
}

@JvmName("toggleIntList")
private fun MutableList<Int>.toggle(button: List<Int>) {
    for (index in button) {
        this[index] -= 1
    }
}

private fun List<Int>.isNegative(): Boolean {
    return this.indices.any { this[it] < 0 }
}

private fun List<Any>.isMatch(other: List<Any>): Boolean {
    return this.indices.all { this[it] == other[it] }
}

private fun String.parseMachine(): Triple<List<Boolean>, List<List<Int>>, List<Int>> {
    val machine = drop(1).takeWhile { it == '.' || it == '#' }.map { it == '#' }
    val buttonList = dropWhile { it != '(' }.takeWhile { it != '{' }
    val buttons = buttonList.split(" ")
        .map { it.drop(1).dropLast(1).toIntList() }
    val joltages = dropWhile { it != '{' }
        .drop(1).dropLast(1).toIntList()

    return Triple(machine, buttons, joltages)
}
