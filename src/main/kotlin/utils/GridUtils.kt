package utils

import java.util.*

fun dijkstra(
    start: Pair<Int, Int>,
    neighbours: (pos: Pair<Int, Int>) -> List<Pair<Int, Int>>,
    weight: (from: Pair<Int, Int>, to: Pair<Int, Int>) -> Int
): Map<Pair<Int, Int>, Int> {
    val distances = mutableMapOf<Pair<Int, Int>, Int>().withDefault { Int.MAX_VALUE }
    val priorityQueue = PriorityQueue<Pair<Pair<Int, Int>, Int>>(compareBy { it.second })
    val visited = mutableSetOf<Pair<Pair<Int, Int>, Int>>()

    priorityQueue.add(Pair(start, 0))
    distances[start] = 0

    while (priorityQueue.isNotEmpty()) {
        val (node, currentDist) = priorityQueue.poll()
        if (visited.add(node to currentDist)) {
            neighbours(node).forEach { adjacent ->
                val dist = weight(node, adjacent)
                val totalDist = currentDist + dist
                if (totalDist <= distances.getValue(adjacent)) {
                    distances[adjacent] = totalDist
                    priorityQueue.add(Pair(adjacent, totalDist))
                } else if (
                    totalDist == distances.getValue(adjacent)
                ) {
                    println("same $adjacent $totalDist")
                }
            }
        }
    }
    return distances
}
