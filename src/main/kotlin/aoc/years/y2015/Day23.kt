package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import kotlin.math.max

fun main() {
    Day23().execute().printResults()
}

private val testInput = """
    inc a
    jio a, +2
    tpl a
    inc a
""".trimIndent()

class Day23 : Day(
    year = 2015,
    day = 23,
    part1 = Part(test = 0L, testInput = testInput),
    part2 = Part(test = null, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        return input.executeProgramm(a = 0, b = 0)["b"]!!
    }

    override fun part2(input: List<String>): Any {
        return input.executeProgramm(a = 1, b = 0)["b"]!!
    }
}

private fun List<String>.executeProgramm(a: Long, b: Long): Map<String, Long> {
    val registers = mutableMapOf("a" to a, "b" to b)

    var index = 0
    while (index in indices) {
        val instruction = this[index]
        when (instruction.take(3)) {
            "hlf" -> {
                val r = instruction.drop(4).take(1)
                registers[r] = max(registers[r]!! / 2, 0)
                index++
            }
            "tpl" -> {
                val r = instruction.drop(4).take(1)
                registers[r] = max(registers[r]!! * 3, 0)
                index++
            }
            "inc" -> {
                val r = instruction.drop(4).take(1)
                registers[r] = max(registers[r]!! + 1, 0)
                index++
            }
            "jmp" -> {
                val offset = instruction.drop(4).trim().toInt()
                index += offset
            }
            "jie" -> {
                val r = instruction.drop(4).take(1)
                val offset = instruction.drop(7).trim().toInt()
                if (registers[r]!! % 2 == 0L) {
                    index += offset
                } else {
                    index++
                }
            }
            "jio" -> {
                val r = instruction.drop(4).take(1)
                val offset = instruction.drop(7).trim().toInt()
                if (registers[r]!! == 1L) {
                    index += offset
                } else {
                    index++
                }
            }
        }
    }

    return registers
}
