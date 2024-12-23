import utils.readInput
import utils.testAndPrint

fun main() {
    fun part1(input: List<String>): Long {
        return input.countPairsStartingWith("t")
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    val testInput = readInput("Day23_test")
    part1(testInput).testAndPrint(7L)
    part2(testInput).testAndPrint()

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

private fun String.parse(): Pair<String, String> = split("-").let { it[0] to it[1] }
private fun List<String>.parse(): List<Pair<String, String>> = map { it.parse() }
