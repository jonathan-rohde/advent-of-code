import utils.readInput
import utils.testAndPrint
import utils.toLongList

fun main() {
    fun part1(input: List<String>): Long {
        return input.first().toLongList()
            .flatMap { it.splitStone(25) }
            .count().toLong()

    }

    fun part2(input: List<String>): Long {
        return input.first().toLongList()
            .flatMap { it.splitStone(75) }
            .count().toLong()
    }

    val testInput = readInput("Day11_test")
    part1(testInput).testAndPrint(55312L)
    part2(testInput).testAndPrint(1L)

    val input = readInput("Day11")
    part1(input).testAndPrint()
    part2(input).testAndPrint()
}

private fun Long.splitStone(steps: Int): List<Long> {
    return splitStones(steps)
}

private fun Long.splitStones(stepsMissing: Int): List<Long> {
    if (stepsMissing == 0) {
        return listOf(this)
    }
    if (this == 0L) {
        return listOf(1L).flatMap { it.splitStones(stepsMissing - 1) }
    }
    if (this.toString().length % 2 == 0) {
        val string = this.toString()
        val half = string.length / 2
        val firstHalf = string.substring(0, half).toLong(10)
        val secondHalf = string.substring(half).toLong(10)
        return listOf(firstHalf, secondHalf).flatMap { it.splitStones(stepsMissing - 1) }
    }
    return listOf(this * 2024).flatMap { it.splitStones(stepsMissing - 1) }

}
