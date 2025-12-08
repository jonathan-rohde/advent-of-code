package aoc.years.y2025

import aoc.common.Day
import aoc.common.printResults
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class Day08 : Day(year = 2025, day = 8, test = null to null, testLimit1 = 10, limit1 = 1000) {
    override fun part1(input: List<String>, limit: Int): Long {
        val nodes = input.map { it.parseNode() }

        val circuits = nodes.connectCircuits(limit)
        return circuits.sortedByDescending { it.size }.take(3).fold(1L) { acc, set -> acc * set.size }
    }

    override fun part2(input: List<String>): Long {
//        return -1L
        val nodes = input.map { it.parseNode() }
        val (a, b) = nodes.connectAllCircuits() ?: return -1
        return a.x.toLong() * b.x.toLong()
    }
}

fun main() {
    Day08().execute().printResults()
}

private fun List<CircuitNode>.createMap(): Map<Pair<CircuitNode, CircuitNode>, Double> {
    val map = mutableMapOf<Pair<CircuitNode, CircuitNode>, Double>()
    indices.forEach { i ->
        (i + 1 until size).forEach { j ->
            val nodeA = this[i]
            val nodeB = this[j]
            val pair = sortedPair(nodeA, nodeB)
            val distance = nodeA.distanceTo(nodeB)
            map[pair] = distance
        }
    }
    return map
}

private fun String.parseNode(): CircuitNode {
    val (x, y, z) = split(",").map { it.toInt() }
    return CircuitNode(x, y, z)
}

private data class CircuitNode(
    val x: Int,
    val y: Int,
    val z: Int
) {
    fun distanceTo(other: CircuitNode) =
        sqrt((x - other.x).toDouble().pow(2) + (y - other.y).toDouble().pow(2) + (z - other.z).toDouble().pow(2))
}

private fun sortedPair(a: CircuitNode, b: CircuitNode): Pair<CircuitNode, CircuitNode> =
    listOf(a, b).sortedWith(compareBy({ it.x }, { it.y }, { it.z })).let { it[0] to it[1] }

private fun List<CircuitNode>.connectCircuits(limit: Int): List<Set<CircuitNode>> {
    val visited = mutableSetOf<Pair<CircuitNode, CircuitNode>>()
    var circuits = mutableListOf<MutableSet<CircuitNode>>()

    var iteration = 0
    while (iteration < limit) {
        val pair = findClosestPair(visited) ?: return circuits
        visited.add(pair)
        val (nodeA, nodeB) = pair
        val circuitA = circuits.find { nodeA in it }
        val circuitB = circuits.find { nodeB in it }

        when {
            circuitA != null && circuitB != null -> {
                if (circuitA != circuitB) {
                    // merge circuits
                    circuitA.addAll(circuitB)
                    circuits = circuits.filter { it != circuitB }.toMutableList()
                }
                iteration++
            }

            circuitA == null && circuitB == null -> {
                // new circuit
                circuits.add(mutableSetOf(nodeA, nodeB))
                iteration++
            }

            circuitA != null -> {
                // add to A
                circuitA.add(nodeB)
                iteration++
            }

            circuitB != null -> {
                // add to B
                circuitB.add(nodeA)
                iteration++
            }
        }
    }

    return circuits
}

private fun List<CircuitNode>.connectAllCircuits(): Pair<CircuitNode, CircuitNode>? {
    val visited = mutableSetOf<Pair<CircuitNode, CircuitNode>>()
    var circuits = mutableSetOf<MutableSet<CircuitNode>>()

    var iteration = 0
    while (true) {
        val pair = findClosestPair(visited) ?: return null
        visited.add(pair)
        val (nodeA, nodeB) = pair
        val circuitA = circuits.find { nodeA in it }
        val circuitB = circuits.find { nodeB in it }

        when {
            circuitA != null && circuitB != null -> {
                if (circuitA != circuitB) {
                    // merge circuits
                    circuitA.addAll(circuitB)
                    circuits = circuits.filter { it != circuitB }.toMutableSet()
                }
                iteration++
            }

            circuitA == null && circuitB == null -> {
                // new circuit
                circuits.add(mutableSetOf(nodeA, nodeB))
                iteration++
            }

            circuitA != null -> {
                // add to A
                circuitA.add(nodeB)
                iteration++
            }

            circuitB != null -> {
                // add to B
                circuitB.add(nodeA)
                iteration++
            }
        }

        if (iteration > 10 && circuits.size == 1 && circuits.first().size == this.size) {
            println("All connected after $iteration iterations")
            println(pair)
            return pair
        }
    }
}

private fun List<CircuitNode>.findClosestPair(
    visited: Set<Pair<CircuitNode, CircuitNode>>
): Pair<CircuitNode, CircuitNode>? {
    var minDistance = Double.MAX_VALUE
    var closestPair: Pair<CircuitNode, CircuitNode>? = null

    indices.forEach { i ->
        (i + 1 until size).forEach { j ->
            val nodeA = this[i]
            val nodeB = this[j]
            val pair = sortedPair(nodeA, nodeB)
            if (pair in visited) return@forEach

            val distance = nodeA.distanceTo(nodeB)
            if (distance < minDistance) {
                minDistance = distance
                closestPair = pair
            }
        }
    }

    return closestPair
}
