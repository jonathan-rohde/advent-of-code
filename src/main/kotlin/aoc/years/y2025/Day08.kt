package aoc.years.y2025

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

private val testInput = """
    162,817,812
    57,618,57
    906,360,560
    592,479,940
    352,342,300
    466,668,158
    542,29,236
    431,825,988
    739,650,466
    52,470,668
    216,146,977
    819,987,18
    117,168,530
    805,96,715
    346,949,466
    970,615,88
    941,993,340
    862,61,35
    984,92,344
    425,690,689
""".trimIndent()

class Day08 : Day(
    year = 2025,
    day = 8,
    part1 = Part(test = 40L, testLimit = 10, limit = 1000, testInput = testInput),
    part2 = Part(test = 25272L, testInput = testInput)
) {
    override fun part1(input: List<String>, limit: Int): Long {
        val nodes = input.map { it.parseNode() }
        val smallestJoints = nodes.createDistanceList().take(limit)
        val (circuits, _) = smallestJoints.connectCircuits()
        return circuits.sortedByDescending { it.size }.take(3).fold(1L) { acc, set -> acc * set.size }
    }

    override fun part2(input: List<String>): Long {
        val nodes = input.map { it.parseNode() }
        val smallestJoints = nodes.createDistanceList()
        return smallestJoints.connectCircuits { circuits -> circuits.size == 1 && circuits.first().size == nodes.size }.second.let { (a, b) ->
            a.x.toLong() * b.x.toLong()
        }
    }
}

fun main() {
    Day08().execute().printResults()
}

private fun List<CircuitNode>.createDistanceList() = createMap().entries.sortedBy { it.value }.map { it.key }

private fun List<CircuitNode>.createMap(): Map<Pair<CircuitNode, CircuitNode>, Long> {
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
    return map
}

private fun String.parseNode(): CircuitNode {
    val (x, y, z) = split(",").map { it.toInt() }
    return CircuitNode(x, y, z)
}

private data class CircuitNode(
    val x: Int, val y: Int, val z: Int
) {
    fun distanceTo(other: CircuitNode) = (x - other.x).square() + (y - other.y).square() + (z - other.z).square()
}

private fun Int.square(): Long = this.toLong().let { it * it }

private fun sortedPair(a: CircuitNode, b: CircuitNode): Pair<CircuitNode, CircuitNode> =
    listOf(a, b).sortedWith(compareBy({ it.x }, { it.y }, { it.z })).let { it[0] to it[1] }

private fun List<Pair<CircuitNode, CircuitNode>>.connectCircuits(
    breakCondition: (MutableList<MutableSet<CircuitNode>>) -> Boolean = { false }
): Pair<List<Set<CircuitNode>>, Pair<CircuitNode, CircuitNode>> {
    val circuits: MutableList<MutableSet<CircuitNode>> = mutableListOf()

    var lastPair: Pair<CircuitNode, CircuitNode> = first()
    forEach { (nodeA, nodeB) ->
        val circuitA = circuits.find { nodeA in it }
        val circuitB = circuits.find { nodeB in it }

        if (circuitA != null && circuitB != null) {
            if (circuitA != circuitB) {
                circuits.merge(circuitA, circuitB)
            }
        } else if (circuitA == null && circuitB == null) {
            // new circuit
            circuits.add(mutableSetOf(nodeA, nodeB))
        } else if (circuitA != null) {
            // add to A
            circuitA.add(nodeB)
        } else circuitB?.add(nodeA)
        lastPair = nodeA to nodeB

        if (breakCondition(circuits)) {
            return circuits to (nodeA to nodeB)
        }
    }

    return circuits to lastPair
}

private fun MutableCollection<MutableSet<CircuitNode>>.merge(a: MutableSet<CircuitNode>, b: MutableSet<CircuitNode>) {
    a.addAll(b)
    this.remove(b)
}
