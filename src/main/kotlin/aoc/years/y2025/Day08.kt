package aoc.years.y2025

import aoc.common.Day
import aoc.common.printResults
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.measureTimedValue

class Day08 : Day(year = 2025, day = 8, test = 40L to 25272L, testLimit1 = 10, limit1 = 1000) {
    override fun part1(input: List<String>, limit: Int): Long {
        return measureTimedValue {
            val nodes = input.map { it.parseNode() }
            val smallestJoints = nodes.createMap().entries.sortedBy { it.value }.take(limit).map { it.key }
            smallestJoints.connectCircuits().sortedByDescending { it.size }.take(3).fold(1L) { acc, set -> acc * set.size }
        }.also { println("part1: ${it.duration}") }.value
    }

    override fun part2(input: List<String>): Long {
        return measureTimedValue {
            val nodes = input.map { it.parseNode() }
            val smallestJoints = nodes.createMap()
                .entries
                .sortedBy { it.value }
                .map { it.key }
            nodes.connectAllCircuits(smallestJoints)?.let {
                val (a, b) = it
                a.x.toLong() * b.x.toLong()
            }!!
        }.also { println("part2: ${it.duration}") }.value
    }
}

fun main() {
    Day08().execute().printResults()
}

private fun List<CircuitNode>.createMap(): Map<Pair<CircuitNode, CircuitNode>, Long> {
    return measureTimedValue {
        val map = mutableMapOf<Pair<CircuitNode, CircuitNode>, Long>()
        indices.forEach { i ->
            (i + 1 until size).forEach { j ->
                val nodeA = this[i]
                val nodeB = this[j]
                val pair = sortedPair(nodeA, nodeB)
                val distance = nodeA.distanceTo(nodeB)
                map[pair] = distance
            }
        }
        map
    }.also { println("createMap: ${it.duration}") }.value
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
        (x - other.x).square() + (y - other.y).square() + (z - other.z).square()
}

private fun Int.square(): Long = this.toLong().let { it * it }

private fun sortedPair(a: CircuitNode, b: CircuitNode): Pair<CircuitNode, CircuitNode> =
    listOf(a, b).sortedWith(compareBy({ it.x }, { it.y }, { it.z })).let { it[0] to it[1] }

private fun List<Pair<CircuitNode, CircuitNode>>.connectCircuits(): List<Set<CircuitNode>> {
    var circuits = mutableListOf<MutableSet<CircuitNode>>()

    this.forEach {
        val (nodeA, nodeB) = it
        val circuitA = circuits.find { nodeA in it }
        val circuitB = circuits.find { nodeB in it }

        when {
            circuitA != null && circuitB != null -> {
                if (circuitA != circuitB) {
                    // merge circuits
                    circuitA.addAll(circuitB)
                    circuits = circuits.filter { it != circuitB }.toMutableList()
                }
            }

            circuitA == null && circuitB == null -> {
                // new circuit
                circuits.add(mutableSetOf(nodeA, nodeB))
            }

            circuitA != null -> {
                // add to A
                circuitA.add(nodeB)
            }

            circuitB != null -> {
                // add to B
                circuitB.add(nodeA)
            }
        }
    }

    return circuits
}

private fun List<CircuitNode>.connectAllCircuits(joints: List<Pair<CircuitNode, CircuitNode>>): Pair<CircuitNode, CircuitNode>? {
    var circuits = mutableSetOf<MutableSet<CircuitNode>>()

    var iteration = 0
    joints.forEach {
        val (nodeA, nodeB) = it
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
            return it
        }
    }
    return null
}
