package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults

fun main() {
    Day13().execute().printResults()
}

private val testInput = """
    Alice would gain 54 happiness units by sitting next to Bob.
    Alice would lose 79 happiness units by sitting next to Carol.
    Alice would lose 2 happiness units by sitting next to David.
    Bob would gain 83 happiness units by sitting next to Alice.
    Bob would lose 7 happiness units by sitting next to Carol.
    Bob would lose 63 happiness units by sitting next to David.
    Carol would lose 62 happiness units by sitting next to Alice.
    Carol would gain 60 happiness units by sitting next to Bob.
    Carol would gain 55 happiness units by sitting next to David.
    David would gain 46 happiness units by sitting next to Alice.
    David would lose 7 happiness units by sitting next to Bob.
    David would gain 41 happiness units by sitting next to Carol.
""".trimIndent()

class Day13 : Day(
    year = 2015,
    day = 13,
    part1 = Part(test = 330, testInput = testInput),
    part2 = Part(test = 286, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        val data = input.map { it.parseHappiness() }
            .groupBy { it.name }
            .map { (name, targets) -> targets.join() }

        val findBestSeatOrder = data.findBestSeatOrder()
        return findBestSeatOrder
            .happiness()
    }

    override fun part2(input: List<String>): Any {
        val data = input.map { it.parseHappiness() }
            .groupBy { it.name }
            .map { (name, targets) -> targets.join() }
            .map { Person(it.name, it.happiness + ("me" to 0)) }
        val dataNew = data + Person("me", data.associate { it.name to 0 })

        val findBestSeatOrder = dataNew.findBestSeatOrder()
        return findBestSeatOrder
            .happiness()
    }
}

data class Person(val name: String, val happiness: Map<String, Int>)

private val lineRegex =
    "([a-zA-Z]+) would (gain|lose) ([0-9]+) happiness units by sitting next to ([a-zA-Z]+)".toRegex()

private fun String.parseHappiness(): Person {
    val match = lineRegex.find(this)!!
    val (name, rating, value, target) = match.destructured
    val sign = if (rating == "gain") 1 else -1
    return Person(name, mapOf(target to sign * value.toInt()))
}

private fun List<Person>.join(): Person {
    require(map { it.name }.distinct().size == 1)
    val merged = flatMap { it.happiness.entries }.associate { it.key to it.value }
    return Person(first().name, merged)
}

private fun List<Person>.findBestSeatOrder(): List<Person> {
    return findBestSeatOrder(this.toSet(), emptyList(), 0)
}

private fun List<Person>.happiness(): Int {
    return let { it + it.first() }
        .windowed(2)
        .sumOf {
            val a = it[0]
            val b = it[1]
            a.happiness[b.name]!! + b.happiness[a.name]!!
        }
}

private fun findBestSeatOrder(remain: Set<Person>, current: List<Person>, happiness: Int): List<Person> {
    if (remain.isEmpty()) return current
    return remain.map { next ->
        val newHappiness = if (current.isNotEmpty()) happiness + current.last().happiness[next.name]!! else 0
        val newRemain = remain.filter { it.name != next.name }.toSet()
        val newCurrent = current + next
        findBestSeatOrder(newRemain, newCurrent, newHappiness)
    }.maxBy { it.happiness() }
}