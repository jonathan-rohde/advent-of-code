package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import java.security.MessageDigest
import kotlin.math.abs
import kotlin.math.min
import kotlin.text.fold

fun main() {
    Day05().execute().printResults()
}

private val testInput = """
    ugknbfddgicrmopn
    aaa
    jchzalrnumimnmhp
    haegwjzuvuyypxyu
    dvszwmarrgswjxmb
""".trimIndent()

private val testInput2 = """
    qjhvhtzxzqqjkmpb
    xxyxx
    uurcxstgmygtbstg
    ieodomkazucvgmuy
""".trimIndent()

class Day05 : Day(
    year = 2015,
    day = 5,
    part1 = Part(test = 2, testInput = testInput),
    part2 = Part(test = 2, testInput = testInput2),
) {
    override fun part1(input: List<String>): Any {
        return input.count { it.isNice() }
    }
    override fun part2(input: List<String>): Any {
        return input.count { it.isImprovedNice() }
    }
}

private val notAllowed = setOf("ab", "cd", "pq", "xy")
private val vowls = setOf('a', 'e', 'i', 'o', 'u')
private fun String.isNice(): Boolean {
    val foundVowls = mutableSetOf<Int>()
    var pairs = false
    windowed(2, partialWindows = true).forEachIndexed { index, it ->
        if (it in notAllowed) {
            return false
        }
        if (it.length == 2 && it[0] == it[1]) pairs = true
        if (it[0] in vowls) foundVowls.add(index)
    }

    return (foundVowls.size >= 3 && pairs)
}
private fun String.isImprovedNice(): Boolean {
    val foundThreeLetter = checkThreeLetters()
    val findPair = checkPairWithoutOverlap()
    return foundThreeLetter && findPair
}

private fun String.checkThreeLetters(): Boolean {
    windowed(3).forEach {
        if (it[0] == it[2]) return true
    }
    return false
}

private fun String.checkPairWithoutOverlap(): Boolean {
    return windowed(2).mapIndexedNotNull { index, possiblePattern ->
        val occurences = lastIndexOf(possiblePattern)
        if (occurences - index > 1) possiblePattern
        else null
    }.any()
}