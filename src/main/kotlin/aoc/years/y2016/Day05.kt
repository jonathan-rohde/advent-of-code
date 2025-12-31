package aoc.years.y2016

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import java.security.MessageDigest

fun main() {
    Day05().execute().printResults()
}

private val testInput = """
    abc
""".trimIndent()


class Day05 : Day(
    year = 2016,
    day = 5,
    part1 = Part(test = "18f47a30", testInput = testInput),
    part2 = Part(test = "05ace8e3", testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        return input[0].toPassword()
    }

    override fun part2(input: List<String>): Any {
        return input[0].toExtendedPassword()
    }
}

private fun String.toPassword(): String {
    var result = ""
    var start = 0L
    repeat(8) {
        val (hash, next) = nextPassword(start)
        result += hash[5]
        start = next + 1
    }
    return result
}

private var allowedDigits = setOf('0', '1', '2', '3', '4', '5', '6', '7')
private fun String.toExtendedPassword(): String {
    val result = MutableList(8) {' '}
    var start = 0L
    while (result.contains(' ')) {
        val (hash, next) = nextPassword(start)
        start = next + 1
        val position = hash[5].takeIf { it in allowedDigits }?.digitToInt()
            ?: continue
        if (result[position] != ' ') continue
        result[position] = hash[6]
    }
    return result.joinToString("")
}

private fun String.nextPassword(start: Long): Pair<String, Long> {
    return (start..Long.MAX_VALUE).firstNotNullOf {
        (("$this$it".toMD5()) to it).takeIf { it.first.startsWith("00000") }
    }
}


private val md5 = MessageDigest.getInstance("MD5")
private fun String.toMD5() = md5.digest(toByteArray()).toHexString()
