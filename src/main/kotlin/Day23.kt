import utils.readInput
import utils.testAndPrint
import java.util.PriorityQueue

fun main() {
    fun part1(input: List<String>): Long {
        return input.countPairsStartingWith("t")
    }

    fun part2(input: List<String>): String {
        return input.largestSet().sorted().joinToString(",")
    }

    val testInput = readInput("Day23_test")
    part1(testInput).testAndPrint(7L)
    part2(testInput).testAndPrint("co,de,ka,ta")

    val input = readInput("Day23")
    part1(input).testAndPrint()
    part2(input).testAndPrint()
}

private fun List<String>.countPairsStartingWith(t: String): Long {
    val pairs = parse()
    val network: MutableMap<String, List<String>> = mutableMapOf<String, List<String>>().withDefault { emptyList() }
    pairs.forEach { (a, b) ->
        network[a] = network.getValue(a) + b
        network[b] = network.getValue(b) + a
    }

    val result = mutableSetOf<Triple<String, String, String>>()

    network.entries.forEach { (a, listA) ->
        network.entries.forEach { (b, listB) ->
            network.entries.forEach { (c, listC) ->
                if (listA.contains(b) && listB.contains(c) && listC.contains(a)) {
                    val (x, y, z) = listOf(a, b, c).sorted().distinct()
                    result.add(Triple(x, y, z))
                }

            }
        }
    }

    return result.count { (a, b, c) ->
        listOf(a, b, c).any { it.startsWith(t) }
    }.toLong()
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
        println("Check $node")
        val neighbourQueue = PriorityQueue<String>()
        pairs.mapNotNull { (a, b) ->
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
                pairs.hasConnection(node, it)
            }
            .forEach { neighbourQueue.add(it) }
        val group = mutableSetOf(node)
        while (neighbourQueue.isNotEmpty()) {
            val neighbour = neighbourQueue.poll()
            if (!group.all { pairs.hasConnection(it, neighbour) }) continue
            if (group.contains(neighbour)) continue
            visited.add(neighbour)
            group.add(neighbour)
            pairs.mapNotNull { (a, b) ->
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
                    group.all { member -> pairs.hasConnection(member, it) }
                }
                .forEach { neighbourQueue.add(it) }
        }
        if (group.size > result.size) {
            result = group
        }
    }
    return result
}

private fun List<Pair<String, String>>.hasConnection(a: String, b: String): Boolean {
    return any { it.hasConnection(a, b) }
}

private fun Pair<String, String>.hasConnection(a: String, b: String): Boolean {
    return this == a to b || this == b to a
}

private fun String.parse(): Pair<String, String> = split("-").let { it[0] to it[1] }
private fun List<String>.parse(): List<Pair<String, String>> = map { it.parse() }
