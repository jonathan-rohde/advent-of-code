package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import java.security.MessageDigest
import kotlin.math.min
import kotlin.text.fold

fun main() {
    Day04().execute().printResults()
}

private val testInput = """
    abcdef
""".trimIndent()

class Day04 : Day(
    year = 2015,
    day = 4,
    part1 = Part(test = 609043L, testInput = testInput),
    part2 = Part(test = 6742839L, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        return with (input.first()) {
            (0..Long.MAX_VALUE).first {
                "$this$it".toMD5().startsWith("00000")
            }
        }
    }
    override fun part2(input: List<String>): Any {
        return with (input.first()) {
            (0..Long.MAX_VALUE).first {
                "$this$it".toMD5().startsWith("000000")
            }
        }
    }
}

private val md5 = MessageDigest.getInstance("MD5")
private fun String.toMD5() = md5.digest(toByteArray()).toHexString()
