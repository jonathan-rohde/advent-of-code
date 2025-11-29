package aoc.years.y2023

import aoc.common.Day
import aoc.common.printResults
import utils.toLongList

class Day05 : Day(year = 2023, day = 5, test = 35L to 46L) {
    override fun part1(input: List<String>): Any {
        val seedMap = input.parseSeedMap(::parseSeeds)
        return seedMap.findSmallesLocation()
    }

    override fun part2(input: List<String>): Any {
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

private fun SeedMap.findSmallesLocation(): Long {
    val min = minimum()
    val max = maximum()
    val foundSeed = (min..max).asSequence()
        .map { possibleLoc ->
            listOf(location, humidiy, temperature, light, water, fertilizer, soil)
                .fold(possibleLoc) { num, map ->
                    determineSource(num, map)
                }
        }

        .first { seed ->
            seeds.any { it.startInclusive <= seed && it.endInclusive >= seed }
        }
    return listOf(soil, fertilizer, water, light, temperature, humidiy, location)
        .fold(foundSeed) { num, map ->
            determineTarget(num, map)
        }
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
        return value in startInclusive..endInclusive
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