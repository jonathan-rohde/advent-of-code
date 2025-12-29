package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

fun main() {
    Day24().execute().printResults()
}

private val testInput = """
    1
    2
    3
    4
    5
    7
    8
    9
    10
    11
""".trimIndent()

class Day24 : Day(
    year = 2015,
    day = 24,
    part1 = Part(test = 99L, testInput = testInput),
    part2 = Part(test = 44L, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        val weights = input.toWeights()
        return weights.distribute()
    }

    override fun part2(input: List<String>): Any {
        val weights = input.toWeights()
        return weights.distribute(4)
    }
}

private fun List<String>.toWeights(): List<Long> {
    return map { it.toLong() }
}

private fun List<Long>.distribute(count: Int = 3): Long {
    val ideal = sum() / count

    val group1 = fillUntil(ideal)
    return group1.first
}

private fun List<Long>.fillUntil(
    targetWeight: Long,
    qe: Long = 1,
    weight: Long = 0,
    usedBoxes: Set<Long> = emptySet()
): Pair<Long, Int> {
    if (weight == targetWeight) return qe to usedBoxes.size
    if (isEmpty()) return Long.MAX_VALUE to Int.MAX_VALUE
    if (weight > targetWeight) return Long.MAX_VALUE to Int.MAX_VALUE

    val useBox = drop(1).fillUntil(
        targetWeight = targetWeight,
        qe = (qe * this.first()).takeIf { it > 0 } ?: Long.MAX_VALUE,
        weight = (weight + this.first()).takeIf { it > 0 } ?: Long.MAX_VALUE,
        usedBoxes = usedBoxes + first()
    )
    val notUseBox = drop(1).fillUntil(
        targetWeight = targetWeight,
        qe = qe,
        weight = weight,
        usedBoxes
    )

    return listOf(useBox, notUseBox)
        .sortedWith(
            compareBy(
                { it.second }, { it.first }
        ))
        .first()
}
