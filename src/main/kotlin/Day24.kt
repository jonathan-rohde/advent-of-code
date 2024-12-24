import utils.readInput
import utils.testAndPrint

fun main() {
    fun part1(input: List<String>): Long {
        val t = input.parseProblem()
        return t.solve()
    }

    fun part2(input: List<String>): String {
        val system = input.parseProblem()
        val rules = system.second.toList()
        return rules.wrongWires().sorted().joinToString(",")
    }

    val testInput = readInput("Day24_test")
    part1(testInput).testAndPrint(4L)

    val input = readInput("Day24")
    part1(input).testAndPrint()
    part2(input).testAndPrint()
}

private fun Pair<List<Gate>, List<Rule>>.solve(): Long {
    val rules = second.toMutableList()
    val gates = first.associate { it.first to it.second }.toMutableMap()

    while (rules.isNotEmpty()) {
        val nextRule = rules.firstOrNull {
            it.first.first in gates && it.first.third in gates
        } ?: return -1L
        rules.remove(nextRule)
        val (a, op, b) = nextRule.first
        val output = nextRule.second
        val value = when (op) {
            "AND" -> gates[a]!! and gates[b]!!
            "OR" -> gates[a]!! or gates[b]!!
            "XOR" -> gates[a]!! xor gates[b]!!
            else -> error("Invalid operation: $op")
        }
        gates[output] = value
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
    val match =
        "([a-z]+\\d*) (AND|OR|XOR) ([a-z]+\\d*) -> ([a-z]+\\d*)".toRegex().find(this) ?: error("Invalid rule: $this")
    val (a, op, b, c) = match.destructured
    return Triple(a, op, b) to c
}

private fun String.parseGateBool(): Boolean {
    return when {
        this == "1" -> true
        this == "0" -> false
        else -> error("Invalid gate value: $this")
    }
}

private fun Rule.`only least significant z can be xor`(leastSignificant: String): Boolean =
    second.startsWith("z") && first.second != "XOR" && second != leastSignificant

private fun Rule.`xor only on intermediate gates `(): Boolean {
    val (a, op, b) = first
    val output = second
    return op == "XOR" &&
            output[0] !in listOf('x', 'y', 'z') &&
            a[0] !in listOf('x', 'y', 'z') &&
            b[0] !in listOf('x', 'y', 'z')
}

private fun Rule.`and but not on x00 and output is not in a or`(rules: List<Rule>) : Set<String> {
    val (a, op, b) = first
    if (op == "AND" && a != "x00" && b != "x00") {
        return rules.mapNotNull { (swapLeft, _) ->
            val (swapA, swapOp, swapB) = swapLeft
            if ((second == swapA || second == swapB) && swapOp != "OR") {
                second
            } else {
                null
            }
        }.toSet()
    }
    return emptySet()
}

private fun Rule.`xor should swap, if there is a or with remaining operators`(rules: List<Rule>) : Set<String> {
    if (first.second == "XOR") {
        return rules.mapNotNull { otherRule ->
            val (a, op, b) = otherRule.first
            if ((second == a || second == b) && op == "OR") {
                second
            } else {
                null
            }
        }.toSet()
    }
    return emptySet()
}

private fun List<Rule>.wrongWires(): Set<String> {
    val result = mutableSetOf<String>()
    val maxZ = filter { it.second.startsWith("z") }
        .map { it.second }
        .maxByOrNull { it.substring(1).toInt() } ?: error("No z wire found")
    forEach { rule ->
        if (rule.`only least significant z can be xor`(maxZ)) {
            result.add(rule.second)
        }

        if (rule.`xor only on intermediate gates `()) {
            result.add(rule.second)
        }

        result.addAll(rule.`and but not on x00 and output is not in a or`(this))
        result.addAll(rule.`xor should swap, if there is a or with remaining operators`(this))

    }
    return result
}

private typealias Gate = Pair<String, Boolean>
private typealias Rule = Pair<Triple<String, String, String>, String>
