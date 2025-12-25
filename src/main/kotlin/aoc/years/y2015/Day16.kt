package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import kotlin.io.path.fileVisitor

fun main() {
    Day16().execute().printResults()
}

private val testInput = """
    Sue 1: goldfish: 6, trees: 9, akitas: 0
    Sue 2: goldfish: 7, trees: 1, akitas: 0
    Sue 3: cars: 10, akitas: 6, perfumes: 7
""".trimIndent()

class Day16 : Day(
    year = 2015,
    day = 16,
    part1 = Part(test = null, testInput = testInput),
    part2 = Part(test = null, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        val aunts = input.map { it.toAuntSue() }

        return aunts.findClosest(search, AuntSue::matches).name
    }

    override fun part2(input: List<String>): Any {
        return input.map { it.toAuntSue() }.findClosest(search, AuntSue::closeMatch).name
    }
}

private fun List<AuntSue>.findClosest(query: AuntSue, compare: (AuntSue, AuntSue) -> Boolean): AuntSue {
    val candidates =  asSequence()
        .filter { compare(it, query) }
        .toList()

    if (candidates.size > 1) {
        return candidates.maxBy { it.matchesNonNull(query) }
    }

    return candidates.first()
}

private data class AuntSue(
    val name: Int, val children: Int?, val cats: Int?, val samoyeds: Int?,
    val pomeranians: Int?, val akitas: Int?, val vizslas: Int?, val goldfish: Int?,
    val trees: Int?, val cars: Int?, val perfumes: Int?
) {

    fun matchesNonNull(other: AuntSue): Int {
        var result = 0
        if (children != null && children.match(other.children)) {
            result++
        }
        if (cats != null && cats.match(other.cats)) {
            result++
        }
        if (samoyeds != null && samoyeds.match(other.samoyeds)) {
            result++
        }
        if (pomeranians != null && pomeranians.smaller(other.pomeranians)) {
            result++
        }
        if (akitas != null && akitas.match(other.akitas)) {
            result++
        }
        if (vizslas != null && vizslas.match(other.vizslas)) {
            result++
        }
        if (goldfish != null && goldfish.smaller(other.goldfish)) {
            result++
        }
        if (trees != null && trees.greater(other.trees)) {
            result++
        }
        if (cars != null && cars.match(other.cars)) {
            result++
        }
        if (perfumes != null && perfumes.match(other.perfumes)) {
            result++
        }
        return result
    }

    fun matches(other: AuntSue): Boolean {
        return children.match(other.children)
                && cats.match(other.cars)
                && samoyeds.match(other.samoyeds)
                && pomeranians.match(other.pomeranians)
                && akitas.match(other.akitas)
                && vizslas.match(other.vizslas)
                && goldfish.match(other.goldfish)
                && trees.match(other.trees)
                && cars.match(other.cars)
                && perfumes.match(other.perfumes)
    }

    fun closeMatch(other: AuntSue): Boolean {
        return children.match(other.children)
                && cats.greater(other.cars)
                && samoyeds.match(other.samoyeds)
                && pomeranians.smaller(other.pomeranians)
                && akitas.match(other.akitas)
                && vizslas.match(other.vizslas)
                && goldfish.smaller(other.goldfish)
                && trees.greater(other.trees)
                && cars.match(other.cars)
                && perfumes.match(other.perfumes)
    }
}

private fun Int?.match(other: Int?): Boolean {
    return this == null || this == other
}

private fun Int?.smaller(other: Int?): Boolean {
    return this == null || other == null || this < other
}

private fun Int?.greater(other: Int?): Boolean {
    return this == null || other == null || this > other
}

private fun String.toAuntSue(): AuntSue {
    val index = drop(4).takeWhile { it.isDigit() }.toInt()
    val list = dropWhile { it != ':' }.drop(1).split(", ")
    val data = list.associate {
        val (attr, value) = it.split(": ")
        attr.trim() to value
    }
    return AuntSue(
        name = index,
        children = data["children"]?.toInt(),
        cats = data["cats"]?.toInt(),
        samoyeds = data["samoyeds"]?.toInt(),
        pomeranians = data["pomeranians"]?.toInt(),
        akitas = data["akitas"]?.toInt(),
        vizslas = data["vizslas"]?.toInt(),
        goldfish = data["goldfish"]?.toInt(),
        trees = data["trees"]?.toInt(),
        cars = data["cars"]?.toInt(),
        perfumes = data["perfumes"]?.toInt()
    )
}

private val search = AuntSue(
    name = 0,
    children = 3,
    cats = 7,
    samoyeds = 2,
    pomeranians = 3,
    akitas = 0,
    vizslas = 0,
    goldfish = 5,
    trees = 3,
    cars = 2,
    perfumes = 1,
)
