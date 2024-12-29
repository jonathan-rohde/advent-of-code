package aoc.years.y2023

import aoc.common.Day
import aoc.common.printResults
import utils.toLongList

class Day05 : Day(year = 2023, day = 5, test = 35L to null) {
    override fun part1(input: List<String>): Any {
        val seedMap = input.parse()
        return smallestLocation(seedMap, seedMap.seeds)
    }

    override fun part2(input: List<String>): Any {
        return -1
    }

}

fun main() {
    Day05().execute().printResults()
}

private fun List<String>.extractParts(start: Int): Pair<Int, List<Triple<Long, Long, Long>>> {
    val parts = mutableListOf<Triple<Long, Long, Long>>()
    var index = start
    while (index in indices && this[index].isNotBlank()) {
        val (destinationStart, sourceStart, steps) = this[index].toLongList()
        parts.add(Triple(destinationStart, sourceStart, steps))
        index++
    }
    return index to parts
}

private fun List<String>.parse(): SeedMap {
    val seeds = this[0].substring("seeds: ".length).toLongList()
    val seedToSoil = extractParts(3)
    val soilToFertilizer = extractParts(seedToSoil.first + 2)
    val fertlizerToWater = extractParts(soilToFertilizer.first + 2)
    val waterToLight = extractParts(fertlizerToWater.first + 2)
    val lightToTemperature = extractParts(waterToLight.first + 2)
    val temperatureToHumidity = extractParts(lightToTemperature.first + 2)
    val humidityToLocation = extractParts(temperatureToHumidity.first + 2)

    return SeedMap(
        seeds = seeds,
        seedToSoil = seedToSoil.second,
        soilToFertilizer = soilToFertilizer.second,
        fertlizerToWater = fertlizerToWater.second,
        waterToLight = waterToLight.second,
        lightToTemperature = lightToTemperature.second,
        temperatureToHumidity = temperatureToHumidity.second,
        humidityToLocation = humidityToLocation.second
    )
}

private data class SeedMap(
    val seeds: List<Long>,
    val seedToSoil: List<Triple<Long, Long, Long>>,
    val soilToFertilizer: List<Triple<Long, Long, Long>>,
    val fertlizerToWater: List<Triple<Long, Long, Long>>,
    val waterToLight: List<Triple<Long, Long, Long>>,
    val lightToTemperature: List<Triple<Long, Long, Long>>,
    val temperatureToHumidity: List<Triple<Long, Long, Long>>,
    val humidityToLocation: List<Triple<Long, Long, Long>>
)

private fun Triple<Long, Long, Long>.getDestination(seed: Long): Long? {
    if (seed in second .. second + third) {
        val diff = seed - second
        return first + diff
    }
    return null
}

private fun List<Triple<Long, Long, Long>>.getDestination(seed: Long): Long {
    return firstNotNullOfOrNull { it.getDestination(seed) } ?: seed
}

private val seedCache = mutableMapOf<Long, Long>()
private fun SeedMap.getLocationOfSeed(seed: Long): Long {
    return seedCache.getOrPut(seed) {
        seedToSoil.getDestination(seed)
            .let { soilToFertilizer.getDestination(it) }
            .let { fertlizerToWater.getDestination(it) }
            .let { waterToLight.getDestination(it) }
            .let { lightToTemperature.getDestination(it) }
            .let { temperatureToHumidity.getDestination(it) }
            .let { humidityToLocation.getDestination(it) }
    }
}

private fun smallestLocation(seedMap: SeedMap, seeds: List<Long>): Long {
    return seeds.minOf {
        seedMap.getLocationOfSeed(it)
    }
}

