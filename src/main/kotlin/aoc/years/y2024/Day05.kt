package aoc.years.y2024

import aoc.common.Day
import aoc.common.printResults
import utils.toIntList

class Day05 : Day(2024, 5, 143 to 123) {
    override fun part1(input: List<String>): Int {
        val (numbers, orders) = input.parse()
        return part1(numbers, orders)
    }

    fun part1(numbers: List<String>, orders: List<String>): Int {
        val afterRule = readPageNumbersAfter(numbers)
        val beforeRule = readPageNumbersBefore(numbers)
        return orders.sumOf {
            val orderList = it.toIntList()
            if (orderList.isValidOrder(afterRule, beforeRule)) {
                orderList[orderList.size / 2]
            } else {
                0
            }
        }
    }

    override fun part2(input: List<String>): Int {
        val (numbers, orders) = input.parse()
        val afterRule = readPageNumbersAfter(numbers)
        val beforeRule = readPageNumbersBefore(numbers)
        return orders.sumOf {
            val orderList = it.toIntList()
            if (!orderList.isValidOrder(afterRule, beforeRule)) {
                part1(numbers, listOf(orderList.sortByRule(afterRule).joinToString(",")))
            } else {
                0
            }
        }
    }
}

private fun List<String>.parse(): Pair<List<String>, List<String>> {
    val numbers = takeWhile { it.isNotBlank() }
    val orders = drop(numbers.size + 1)
    return numbers to orders
}

fun main() {
    Day05().execute().printResults()
}


private fun readPageNumbersAfter(input: List<String>): Map<Int, List<Int>> = input.map {
    it.split("|").let { (page, order) -> page.toInt() to order.toInt() }
}.groupBy { it.first }.mapValues { (_, list) -> list.map { it.second } }

private fun readPageNumbersBefore(input: List<String>): Map<Int, List<Int>> = input.map {
    it.split("|").let { (page, order) -> page.toInt() to order.toInt() }
}.groupBy { it.second }.mapValues { (_, list) -> list.map { it.first } }

private fun List<Int>.isValidOrder(before: Map<Int, List<Int>>, after: Map<Int, List<Int>>): Boolean {
    return mapIndexed { index, updated ->
        val hasToComeAfter = before[updated] ?: emptyList()
        val hasToComeBefore = after[updated] ?: emptyList()

        subList(0, index).none { hasToComeAfter.contains(it) }
                && subList(index + 1, size).none { hasToComeBefore.contains(it) }
    }.reduce(Boolean::and)
}

private fun List<Int>.sortByRule(hasComeAfter: Map<Int, List<Int>>): List<Int> {
    return sortedWith(
        PageComparator(hasComeAfter)
    )
}

class PageComparator(private val hasToComeAfter: Map<Int, List<Int>>) : Comparator<Int> {
    override fun compare(first: Int, second: Int): Int {
        val hasToComeAfterFirst = hasToComeAfter[first] ?: emptyList()
        val hasToComeAfterSecond = hasToComeAfter[second] ?: emptyList()

        return when {
            hasToComeAfterFirst.contains(second) -> -1
            else -> 0
        }
    }
}
