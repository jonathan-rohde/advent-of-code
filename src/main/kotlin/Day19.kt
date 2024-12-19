import utils.measured
import utils.readInput
import utils.testAndPrint

fun main() {
    fun part1(input: List<String>): Int {
        val (patterns, towels) = input.parseInput()
        return towels.count { it combinationsOf patterns > 0 }
    }

    fun part2(input: List<String>): Long {
        val (patterns, towels) = input.parseInput()
        return towels.sumOf { it combinationsOf patterns }
    }

    val testInput = readInput("Day19_test")
    part1(testInput).testAndPrint(6)
    part2(testInput).testAndPrint(16L)

    patternCache.clear()

    val input = readInput("Day19")
    measured(1) {
        part1(input).testAndPrint()
    }
    measured(2) {
        part2(input).testAndPrint()
    }
}

private val patternCache = mutableMapOf<Pattern, Long>()

private infix fun Towel.combinationsOf(patterns: Patterns): Long {
    if (this in patternCache) return patternCache[this]!!
    if (isBlank()) return 0
    return (patterns
        .filter { startsWith(it) }
        .map { drop(it.length).combinationsOf(patterns) }
        .sumOf { it }
            + patterns.count { it == this })
        .also {
            patternCache[this] = it
        }
}

private fun List<String>.parseInput(): Pair<Patterns, Towels> = first().split(", ") to drop(2)

private typealias Pattern = String
private typealias Patterns = List<Pattern>
private typealias Towel = String
private typealias Towels = List<Towel>
