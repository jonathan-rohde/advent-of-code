import utils.readInput
import utils.testAndPrint

fun main() {
    fun part1(input: List<String>): Long {
        val t = input.parseProblem()
        return t.solve()
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    val testInput = readInput("Day24_test")
    part1(testInput).testAndPrint(4L)
    part2(testInput).testAndPrint()

    val input = readInput("Day24")
    part1(input).testAndPrint()
    part2(input).testAndPrint()
}

private fun Pair<List<Gate>, List<Rule>>.solve(): Long {
    val rules = second.toMutableList()
    val gates = first.associate { it.first to it.second }.toMutableMap()

    while (rules.isNotEmpty()) {
        val nextRule = rules.first {
            it.first.first in gates && it.first.third in gates
        }
        rules.remove(nextRule)
        val (a, op, b) = nextRule.first
        val c = nextRule.second
        val value = when (op) {
            "AND" -> gates[a]!! and gates[b]!!
            "OR" -> gates[a]!! or gates[b]!!
            "XOR" -> gates[a]!! xor gates[b]!!
            else -> error("Invalid operation: $op")
        }
        gates[c] = value
    }

    val result = gates.entries.filter { it.key.startsWith("z") }
        .sortedByDescending { it.key }.map {
            if (it.value) 1L else 0L
        }

    var number = 0L
    result.forEach {
        number = number shl 1
        number += it
    }
    return number
}

private fun List<String>.parseProblem(): Pair<List<Gate>, List<Rule>> {
    val states = takeWhile { it.isNotBlank() }
        .map { it.parseState() }
    val rules = dropWhile { it.isNotBlank() }.drop(1)
        .map { it.parseRule() }
    return states to rules
}

private fun String.parseState(): Gate {
    val match = "([a-z]+\\d+): ([01])".toRegex().find(this) ?: error("Invalid state: $this")
    val (name, value) = match.destructured
    return name to value.parseGateBool()
}

private fun String.parseRule(): Rule {
    val match = "([a-z]+\\d*) (AND|OR|XOR) ([a-z]+\\d*) -> ([a-z]+\\d*)".toRegex().find(this) ?: error("Invalid rule: $this")
    val (a, op, b, c) = match.destructured
    return Triple(a, op, b) to c
}

private fun String.parseGateBool() : Boolean {
    return when {
        this == "1" -> true
        this == "0" -> false
        else -> error("Invalid gate value: $this")
    }
}

private typealias Gate = Pair<String, Boolean>
private typealias Rule = Pair<Triple<String, String, String>, String>
