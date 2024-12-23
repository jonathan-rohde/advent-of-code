import utils.measured
import utils.readInput
import utils.testAndPrint
import java.util.PriorityQueue

fun main() {
    fun part1(input: List<String>): Long {
        neighbourCache.clear()
        connectionCheckCache.clear()
        return input.countPairsStartingWith("t")
    }

    fun part2(input: List<String>): String {
        neighbourCache.clear()
        connectionCheckCache.clear()
        return input.largestSet().sorted().joinToString(",")
    }

    val testInput = readInput("Day23_test")
    part1(testInput).testAndPrint(7L)
    part2(testInput).testAndPrint("co,de,ka,ta")

    val input = readInput("Day23")
    measured(1) {
        part1(input).testAndPrint()
    }
    measured(2) {
        part2(input).testAndPrint()
    }
}

private fun List<String>.countPairsStartingWith(t: String): Long {
    val result = mutableSetOf<Triple<String, String, String>>()

    val pairs = parse()
    val queue = ArrayDeque<Pair<String, String>>()
    pairs.filter { it.first.startsWith(t) || it.second.startsWith(t) }
        .forEach { queue.add(it) }

    while (queue.isNotEmpty()) {
        val node = queue.removeFirst()

        val matchingPairs = pairs.filter { it != node }
            .filter {
                it.first == node.first || it.first == node.second ||
                        it.second == node.first || it.second == node.second
            }

        val (a, b) = node
        matchingPairs.forEach { (c, d) ->
            val (x, y, z) = listOf(a, b, c, d).sorted().distinct()
            if (pairs.hasConnection(x, y) && pairs.hasConnection(y, z) && pairs.hasConnection(z, x)) {
                result.add(Triple(x, y, z))
            }
        }
    }

    return result.size.toLong()
}

private fun List<String>.largestSet(): Set<String> {
    val pairs = parse()
        .map { (a, b) ->
            val (x, y) = listOf(a, b).sorted()
            x to y
        }

    val connections = findConnections(pairs)
    return connections
}

private val neighbourCache = mutableMapOf<String, List<String>>()

private fun List<Pair<String, String>>.getNeighbour(node: String): List<String> {
    return neighbourCache[node] ?: mapNotNull { (a, b) ->
        if (a == node) {
            b
        } else if (b == node) {
            a
        } else {
            null
        }
    }
        .filter {
            // only if it has connection to all in group
            hasConnection(node, it)
        }.also {
            neighbourCache[node] = it
        }
}

private fun findConnections(pairs: List<Pair<String, String>>): Set<String> {
    val visited = mutableSetOf<String>()
    var result = emptySet<String>()
    val nodes = pairs.flatMap { (a, b) -> listOf(a, b) }.toMutableSet()
    val queue = PriorityQueue<String>()
    queue.addAll(nodes)

    while (queue.isNotEmpty()) {
        val node = queue.poll()
        if (visited.contains(node)) continue
        visited.add(node)
        val neighbourQueue = PriorityQueue<String>()
        pairs.getNeighbour(node).forEach { neighbourQueue.add(it) }
        val group = mutableSetOf(node)
        while (neighbourQueue.isNotEmpty()) {
            val neighbour = neighbourQueue.poll()
            if (group.contains(neighbour)) continue
            if (group.any { !pairs.hasConnection(it, neighbour) }) continue
            visited.add(neighbour)
            group.add(neighbour)
            pairs.getNeighbour(neighbour).forEach { neighbourQueue.add(it) }
        }
        if (group.size > result.size) {
            result = group
        }
    }
    return result
}

private val connectionCheckCache = mutableMapOf<Pair<String, String>, Boolean>()

private fun List<Pair<String, String>>.hasConnection(a: String, b: String): Boolean {
    return connectionCheckCache[a to b] ?:
    (contains(a to b) || contains(b to a)).also {
        connectionCheckCache[a to b] = it
    }
}

private fun String.parse(): Pair<String, String> = split("-").let { it[0] to it[1] }
private fun List<String>.parse(): List<Pair<String, String>> = map { it.parse() }
