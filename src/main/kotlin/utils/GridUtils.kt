package utils

import java.util.*
import kotlin.math.abs

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

fun List<Pair<Long, Long>>.gaussArea(includeEdge: Boolean): Long {
    var sum1 = 0L
    var sum2 = 0L

    windowed(2).map {it[0] to it[1]}.forEach { (a, b) ->
        sum1 += a.first * b.second
        sum2 += a.second * b.first
    }



    if (first() != last()) {
        with (last() to first()) {
            val (a, b) = this
            sum1 += a.first * b.second
            sum2 += a.second * b.first
        }
    }

    val edge = if (includeEdge) {
        windowed(2).map {it[0] to it[1]}.sumOf { (a, b) ->
            abs(a.first - b.first) + abs(a.second - b.second)
        } / 2L + 1L
    } else {
        -(windowed(2).map {it[0] to it[1]}.sumOf { (a, b) ->
            abs(a.first - b.first) + abs(a.second - b.second)
        } / 2L)
    }

    return (abs(sum1 - sum2) / 2) + edge
}