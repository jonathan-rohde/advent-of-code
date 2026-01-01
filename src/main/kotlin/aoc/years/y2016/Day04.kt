package aoc.years.y2016

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import utils.toIntList

fun main() {
    Day04().execute().printResults()
}

private val testInput = """
    aaaaa-bbb-z-y-x-123[abxyz]
    a-b-c-d-e-f-g-h-987[abcde]
    not-a-real-room-404[oarel]
    totally-real-room-200[decoy]
    northpoleobjectstorage-0[abcdef]
""".trimIndent()


class Day04 : Day(
    year = 2016,
    day = 4,
    part1 = Part(test = 1514, testInput = testInput),
    part2 = Part(test = 0, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        return input.map { it.toRoom() }
            .filter { it.isValid() }
            .sumOf { it.second }
    }

    override fun part2(input: List<String>): Any {
        return input.map { it.toRoom() }
            .map { it.decipher() }.first { it.first.contains("northpoleobjectstorage") }
            .second
    }
}

typealias RoomName = Triple<String, Int, String>
private fun String.toRoom(): RoomName {
    val name = substring(0, lastIndexOf('-'))
        .filter { it != '-' }
    val id = substring(lastIndexOf('-') + 1).takeWhile { it.isDigit() }.toInt()
    val checksum = substring(indexOf('[') + 1, indexOf(']'))
    return RoomName(name, id, checksum)
}

private fun RoomName.isValid(): Boolean {
    val histogram = first.groupBy { it }.mapValues { it.value.size }
    val checksum = histogram.entries
        .sortedWith { a, b ->
            val sortByCount = a.value.compareTo(b.value)
            if (sortByCount != 0) -sortByCount
            else a.key.compareTo(b.key)
        }
        .take(5)
        .map { it.key }
        .joinToString("")
    return third == checksum
}

private val alphabet = "abcdefghijklmnopqrstuvwxyz"
private fun RoomName.decipher(): RoomName {
    val name = first.map {
        alphabet[(alphabet.indexOf(it) + second) % alphabet.length]
    }.joinToString("")
    return RoomName(name, second, third)
}
