package aoc.years.y2023

import aoc.common.Day
import aoc.common.printResults
import utils.toLongList
import kotlin.math.max
import kotlin.math.min

class Day05 : Day(year = 2023, day = 5, test = 35L to 46L) {
    override fun part1(input: List<String>): Any {
        val seedMap = input.parseSeedMap(::parseSeeds)
        return seedMap.findSmallesLocation()
    }

    override fun part2(input: List<String>): Any {
//        return -1
        val seedMap = input.parseSeedMap(::parseSeeds2)
        return seedMap.findSmallesLocation()
    }

}

fun main() {
    Day05().execute().printResults()
}

private fun determineTarget(num: Long, map: List<Pair<Range, Range>>): Long {
    return map.find { mapRanges -> mapRanges.first.contains(num) }
        ?.let { foundMap ->
            val offset = num - foundMap.first.startInclusive
            foundMap.second.startInclusive + offset
        }
        ?: num
}

private fun determineSource(num: Long, map: List<Pair<Range, Range>>): Long {
    return map.find { mapRanges -> mapRanges.second.contains(num) }
        ?.let { foundMap ->
            val offset = num - foundMap.second.startInclusive
            foundMap.first.startInclusive + offset
        }
        ?: num
}

private fun SeedMap.minimum(): Long =
    listOf(soil, fertilizer, water, light, temperature, humidiy, location)
        .flatten()
        .map { it.second }
        .minBy { it.startInclusive }
        .startInclusive

private fun SeedMap.maximum(): Long =
    listOf(soil, fertilizer, water, light, temperature, humidiy, location)
        .flatten()
        .map { it.second }
        .maxBy { it.endInclusive }
        .endInclusive

private val cache = mutableMapOf<Long, Long>()

private fun SeedMap.findSmallesLocation(): Long {
    val min = minimum()
    val max = maximum()
//    val list = seeds.sortedBy { it.startInclusive }.map {
    val list = (min..max).asSequence()
//        generateSequence(it.startInclusive) { current ->
//            (current + 1).takeIf { next -> next <= it.endInclusive }
//        }
        .map { possibleLoc ->
            possibleLoc.let { num -> determineSource(num, location) }
            .let { num -> determineSource(num, humidiy) }
            .let { num -> determineSource(num, temperature) }
            .let { num -> determineSource(num, light) }
            .let { num -> determineSource(num, water) }
            .let { num -> determineSource(num, fertilizer) }
            .let { num -> determineSource(num, soil) }
        }

        .first { seed ->
            seeds.any { it.startInclusive <= seed && it.endInclusive >= seed }
        }
//    }
    return listOf(list).map { s ->
        determineTarget(s, soil)
            .let { num -> determineTarget(num, fertilizer) }
            .let { num -> determineTarget(num, water) }
            .let { num -> determineTarget(num, light) }
            .let { num -> determineTarget(num, temperature) }
            .let { num -> determineTarget(num, humidiy) }
            .let { num -> determineTarget(num, location) }
    }.min()

//    return seeds.asSequence()
//        .map {
//            generateSequence(it.startInclusive) { current ->
//                (current + 1).takeIf { next -> next <= it.endInclusive }
//            }
//                .map { num -> determineTarget(num, soil) }
//                .map { num -> determineTarget(num, fertilizer) }
//                .map { num -> determineTarget(num, water) }
//                .map { num -> determineTarget(num, light) }
//                .map { num -> determineTarget(num, temperature) }
//                .map { num -> determineTarget(num, humidiy) }
//                .map { num -> determineTarget(num, location) }
//                .min()
//        }
//        .min()
}


















private data class SeedMap(
    val seeds: List<Range>,
    val soil: List<Pair<Range, Range>>,
    val fertilizer: List<Pair<Range, Range>>,
    val water: List<Pair<Range, Range>>,
    val light: List<Pair<Range, Range>>,
    val temperature: List<Pair<Range, Range>>,
    val humidiy: List<Pair<Range, Range>>,
    val location: List<Pair<Range, Range>>,
) {

}
private fun List<String>.createIndexMap(): Map<String, Int> {
    val result = mutableMapOf<String, Int>()
    mapIndexed { i, r ->
        if (r.matches("[a-z]+.*".toRegex())) {
            result[r] = i
        }
    }
    return result
}
private fun parseSeeds(string: String): List<Range> {
    val values = string.substring("seeds: ".length).toLongList()
    return values.map { Range(it, it) }
}
private fun parseSeeds2(string: String): List<Range> {
    val values = string.substring("seeds: ".length).toLongList()
    return values.windowed(size = 2, step = 2).map { Range(it[0], it[0] + it[1] - 1) }
}
private fun List<String>.parseSeedMap(f: (String) -> List<Range>): SeedMap {
    val index = createIndexMap()
    return SeedMap(
        seeds = f(this[0]),
        soil = extractParts(index["seed-to-soil map:"]!!),
        fertilizer = extractParts(index["soil-to-fertilizer map:"]!!),
        water = extractParts(index["fertilizer-to-water map:"]!!),
        light = extractParts(index["water-to-light map:"]!!),
        temperature = extractParts(index["light-to-temperature map:"]!!),
        humidiy = extractParts(index["temperature-to-humidity map:"]!!),
        location = extractParts(index["humidity-to-location map:"]!!)
    )
}

private data class Range(val startInclusive: Long, val endInclusive: Long) {

    fun contains(value: Long): Boolean {
        return value in startInclusive .. endInclusive
    }

    fun offset(value: Long): Int {
        return (startInclusive..endInclusive).mapIndexedNotNull { i, v ->
            if (v == value) {
                i
            } else {
                null
            }
        }.first()
    }
}
private fun List<String>.extractParts(start: Int): List<Pair<Range, Range>> {
    val parts = mutableListOf<Pair<Range, Range>>()
    var index = start + 1
    while (index in indices && this[index].isNotBlank()) {
        val (destinationStart, sourceStart, steps) = this[index].toLongList()
        parts.add(
            Range(sourceStart, sourceStart + steps - 1)
            to
            Range(destinationStart, destinationStart + steps - 1)
        )
        index++
    }
    return parts
}