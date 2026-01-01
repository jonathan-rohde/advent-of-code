package aoc.years.y2016

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import kotlinx.serialization.json.Json
import java.security.MessageDigest

fun main() {
    Day07().execute().printResults()
}

private val testInput = """
    abba[mnop]qrst
    abcd[bddb]xyyx
    aaaa[qwer]tyui
    ioxxoj[asdfgh]zxcvbn
""".trimIndent()

private val testInput2 = """
    aba[bab]xyz
    xyx[xyx]xyx
    aaa[kek]eke
    zazbz[bzb]cdb
""".trimIndent()


class Day07 : Day(
    year = 2016,
    day = 7,
    part1 = Part(test = 2, testInput = testInput),
    part2 = Part(test = 3, testInput = testInput2),
) {
    override fun part1(input: List<String>): Any {
        return input.count { it.canTls() }
    }

    override fun part2(input: List<String>): Any {
        return input.count { it.canSsl() }
    }
}

private val ipRegex = "([a-z]+)(\\[[a-z]+\\])?".toRegex()
private fun String.toParts(): Pair<Sequence<String>, Sequence<String>> {
    val matches = ipRegex.findAll(this)
    val outsides = matches.map { it.groupValues[1] }
    val brackets = matches.map { it.groupValues[2] }.filter { it.isNotBlank() }
    return outsides to brackets
}

private fun String.canTls(): Boolean {
    val (outsides, brackets) = toParts()
    return outsides.any { it.hasABBA()} && brackets.none { it.hasABBA() }
}

private fun String.canSsl(): Boolean {
    val (outsides, brackets) = toParts()
    val aba = outsides.flatMap { it.getABA() }.map { it.toBAB() }
    return brackets.any { network ->
        aba.any { network.contains(it) }
    }
}

private fun String.hasABBA(): Boolean {
    return windowed(4)
        .filter { it.toCharArray().distinct().size == 2 }
        .any { it.substring(0, 2) == it.substring(2).reversed() }
}

private fun String.getABA(): List<String> {
    return windowed(3)
        .filter { it.toCharArray().distinct().size == 2 }
        .filter { it.substring(0, 2) == it.substring(1).reversed() }
}

private fun String.toBAB(): String {
    return "${this[1]}${this[0]}${this[1]}"
}
