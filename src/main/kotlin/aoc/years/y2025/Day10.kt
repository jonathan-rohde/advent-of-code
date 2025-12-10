package aoc.years.y2025

import aoc.common.Day
import aoc.common.printResults
import utils.readInput
import utils.testAndPrint
import utils.toIntList

class Day10 : Day(year = 2025, day = 10, test = 7L to 33L) {
    override fun part1(input: List<String>): Long {
        return input.map { it.parseMachine() }
            .sumOf {
                it.minSteps()
            }
    }

//    override fun part2(input: List<String>): Long {
//        return input.map { it.parseMachine() }
//            .sumOf {
//                it.minStepsJoltage().also {
//                    println(it)
//                    cacheJoltage.clear()
//                }
//            }
//    }
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
    val target = joltages.map { 0 }
    return minStepsJoltage(joltages, target, buttons)
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
private fun minStepsJoltage(current: List<Int>, target: List<Int>, buttons: List<List<Int>>, previousStates: Set<List<Int>> = emptySet()): Long {
    return cacheJoltage.getOrPut(target to buttons) {
        if (current.isMatch(target)) return@getOrPut 0L
        buttons.minOf {
            val next = current.toMutableList()
            next.toggle(it)
            if (next in previousStates || next.isBiggerThan(target)) {
                return@minOf Integer.MAX_VALUE.toLong()
            }
            val states = previousStates.toMutableSet()
            states.add(current)
            1 + minStepsJoltage(next, target, buttons, states)
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

private fun List<Int>.isBiggerThan(other: List<Int>): Boolean {
    return this.indices.any { this[it] > other[it] }
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
