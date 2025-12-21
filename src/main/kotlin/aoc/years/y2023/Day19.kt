package aoc.years.y2023

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import kotlin.math.max
import kotlin.math.min

private val testInput = """
    px{a<2006:qkq,m>2090:A,rfg}
    pv{a>1716:R,A}
    lnx{m>1548:A,A}
    rfg{s<537:gd,x>2440:R,A}
    qs{s>3448:A,lnx}
    qkq{x<1416:A,crn}
    crn{x>2662:A,R}
    in{s<1351:px,qqz}
    qqz{s>2770:qs,m<1801:hdj,R}
    gd{a>3333:R,R}
    hdj{m>838:A,pv}

    {x=787,m=2655,a=1222,s=2876}
    {x=1679,m=44,a=2067,s=496}
    {x=2036,m=264,a=79,s=2244}
    {x=2461,m=1339,a=466,s=291}
    {x=2127,m=1623,a=2188,s=1013}
""".trimIndent()

class Day19 : Day(
    year = 2023,
    day = 19,
    part1 = Part(test = 19114, testInput = testInput),
    part2 = Part(test = 167409079868000L, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        val (map, list) = input.parse()

        return list.map {
            it to map.handle(it)
        }.filter { it.second }
            .sumOf { it.first.score() }

    }

//    override fun part2(input: List<String>): Any {
//        val (map, _) = input.parse()
//
//        return map.countCombinations()
//
//    }
}

fun main() {
    Day19().execute().printResults()
}


private fun Map<String, List<Rule>>.handle(part: MetalPart): Boolean {
    var next = this["in"]!!.process(part)
    while (next != "A" && next != "R") {
        next = this[next]!!.process(part)
    }

    return next == "A"
}

//private fun Map<String, List<Rule>>.combinations(start: String, part: MetalPart): List<MetalPart> {
//    val accepted = mutableListOf<MetalPart>()
//
//
//    var rules = this[start]!!
//    var remaining = ArrayDeque<MetalPart>()
//    remaining.add(part)
//    while (remaining.isNotEmpty()) {
//        val next = remaining.removeFirst()
//
//        if (rules.isEmpty()) break
//
//        val nextRule = rules.first()
//        val rec = when (nextRule) {
//            is DirectRule -> combinations(nextRule.move(part), part)
//            is ComparisonRule -> {
//                val checkRange = nextRule.accessor(part)
//                val (include, exclude) = nextRule.range.split(next, checkRange, nextRule.source)
//            }
//        }
//
////        val first = remaining.first()
////
////        when (first) {
////            is DirectRule -> combinations(first.move(part), part)
////            is ComparisonRule -> {
////
////            }
////        }
//    }
//
//    return accepted
//}
//
//private fun IntRange.split(part: MetalPart, checkRange: IntRange, source: String): Pair<IntRange, List<IntRange>> {
//    val intersection = max(this.first, checkRange.first)..min(this.last, checkRange.last)
//    val nonIntersection = mutableListOf<IntRange>()
//    val below = min(this.start, checkRange.start)..max(this.last, intersection.)
//}

private val compareRegex = "([xmas])([><])([0-9]+):([a-zA-Z]+)".toRegex()
private val nextRegex = "[a-zA-Z]+".toRegex()

private interface Rule {
    fun move(part: MetalPart): String
}

private class DirectRule(val target: String) : Rule {
    override fun move(part: MetalPart): String {
        return target
    }
}

private class ComparisonRule(val accessor: (MetalPart) -> IntRange, val range: IntRange, val target: String, val source: String): Rule {
    override fun move(part: MetalPart): String {
        val partRange = accessor(part)
        if (partRange.first in range && partRange.last in range) {
            return target
        }
        return "R"
    }

}

private fun List<Rule>.process(part: MetalPart): String {
    forEach { rule ->
        val target = rule.move(part)
        if (target != "R") {
            return target
        }
    }
    return "R"
}

private data class MetalPart(val x: IntRange, val m: IntRange, val a: IntRange, val s: IntRange) {
    fun score(): Int {
        return x.start + m.start + a.start + s.start
    }
}

private fun List<String>.parse(): Pair<Map<String, List<Rule>>, List<MetalPart>> {
    val rules = takeWhile { !it.isBlank() }
    val inputs = takeLastWhile { !it.isBlank() }
        .map { line ->
            val parts = line.drop(1).dropLast(1).split(",")
            val x = parts.first { it[0] == 'x' }.removePrefix("x=").toInt()
            val m = parts.first { it[0] == 'm' }.removePrefix("m=").toInt()
            val a = parts.first { it[0] == 'a' }.removePrefix("a=").toInt()
            val s = parts.first { it[0] == 's' }.removePrefix("s=").toInt()
            MetalPart(x = x.toRange(), m = m.toRange(), a = a.toRange(), s = s.toRange())
        }

    val map = rules.associate { line ->
        val key = line.takeWhile { it != '{' }
        val value = line.dropLast(1).takeLastWhile { it != '{' }
            .split(",")
            .map { rule -> rule.parseRule() }
        key to value
    }

    return map to inputs
}

private fun Int.toRange(): IntRange = this..this

private fun String.parseRule(): Rule {
    if (matches(nextRegex)) {
        return DirectRule(this)
    }
    val matcher = compareRegex.findAll(this).first()
    val variable = matcher.groupValues[1]
    val comp = matcher.groupValues[2]
    val value = matcher.groupValues[3].toInt()
    val target = matcher.groupValues[4]
    val content = when (variable) {
        "x" -> MetalPart::x
        "m" -> MetalPart::m
        "a" -> MetalPart::a
        "s" -> MetalPart::s
        else -> throw IllegalArgumentException("Unknown variable $variable")
    }

    val comparator = when (comp) {
        "<" -> (1 until value)
        ">" -> (value + 1 .. 4000)
        else -> IntRange.EMPTY
    }

    return ComparisonRule(
        content, comparator, target, variable
    )
}