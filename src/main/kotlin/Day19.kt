import utils.readInput
import utils.testAndPrint
import utils.toIntList
import java.util.*

fun main() {
    fun part1(input: List<String>, dimensions: Int, amount: Int): Long {
        val flakes = input.parseInput(amount)
        val distances = flakes.dijkstra(dimensions)
//        printFlakes(flakes, dimensions)
//        printLength(distances, dimensions)
        return distances[SnowFlake(dimensions - 1, dimensions - 1)]!!.toLong()
    }

    fun part2(input: List<String>, dimension: Int): SnowFlake {
        val flakes = input.parseInput(input.size)
        for (i in 0 .. (dimension * dimension)) {
            val distances = flakes.take(i).toSet().dijkstra(dimension)
            if (SnowFlake(dimension - 1, dimension - 1) !in distances) {
                return flakes.toList()[i - 1]
            }
        }
        return SnowFlake(0, 0)

    }

    val testInput = readInput("Day19_test")
    part1(testInput, 7, 12).testAndPrint(22L)
    part2(testInput, 7).testAndPrint(6 to 1)

    val input = readInput("Day19")
    part1(input, 71, 1024).testAndPrint()
    part2(input, 71).testAndPrint()
}

private fun List<String>.parseInput(amount: Int): Set<SnowFlake> {
    return take(amount).map { line -> line.toIntList() }
        .mapTo(mutableSetOf()) { (x, y) -> SnowFlake(x, y) }
}

private typealias SnowFlake = Pair<Int, Int>

private fun Set<SnowFlake>.dijkstra(dimension: Int): Map<SnowFlake, Int> {
    val distances = mutableMapOf<SnowFlake, Int>().withDefault { Int.MAX_VALUE }
    val priorityQueue = PriorityQueue<Pair<SnowFlake, Int>>(compareBy { it.second })
    val visited = mutableSetOf<Pair<SnowFlake, Int>>()

    priorityQueue.add(Pair(SnowFlake(0, 0), 0))
    distances[SnowFlake(0, 0)] = 0

    while (priorityQueue.isNotEmpty()) {
        val (node, currentDist) = priorityQueue.poll()
        if (visited.add(node to currentDist)) {
            listOf(
                SnowFlake(node.first + 1, node.second),
                SnowFlake(node.first - 1, node.second),
                SnowFlake(node.first, node.second + 1),
                SnowFlake(node.first, node.second - 1)
            ).filter {
                it.first >= 0
                        && it.second >= 0
                        && it.first < dimension
                        && it.second < dimension
                        && !contains(it)
            }
                .forEach { neighbour ->
                    val totalDist = currentDist + 1
                    if (totalDist <= distances.getValue(neighbour)) {
                        distances[neighbour] = totalDist
                        priorityQueue.add(Pair(neighbour, totalDist))
                    }
                }
        }
    }
    return distances
}

private fun printFlakes(flakes: Set<SnowFlake>, dimension: Int) {
    for (y in 0 until dimension) {
        for (x in 0 until dimension) {
            if (SnowFlake(x, y) in flakes) {
                print("#")
            } else {
                print(".")
            }
        }
        println()
    }
}


private fun printLength(distances: Map<SnowFlake, Int>, dimension: Int) {
    val pad = distances.values.max().toString().length + 2
    for (y in 0 until dimension) {
        for (x in 0 until dimension) {
            if (SnowFlake(x, y) in distances) {
                print(distances[SnowFlake(x, y)]!!.toString().padStart(pad))
            } else {
                print(".".padStart(pad))
            }
        }
        println()
    }
}
