import utils.println
import utils.readInput
import utils.toIntList

fun main() {
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

    fun part2(numbers: List<String>, orders: List<String>): Int {
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

    // Or read a large test input from the `src/DayXX_test.txt` file:
    val testNumbers = readInput("Day05_numbers_test")
    val testOrders = readInput("Day05_orders_test")
    check(part1(testNumbers, testOrders) == 143)
    check(part2(testNumbers, testOrders) == 123)

    // Read the input from the `src/DayXX.txt` file.
    val numbers = readInput("Day05_numbers")
    val orders = readInput("Day05_orders")
    part1(numbers, orders).println()
    part2(numbers, orders).println()
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
