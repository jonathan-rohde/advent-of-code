package aoc.years.y2024

import aoc.common.Day
import aoc.common.printResults
import utils.toLongList
import kotlin.math.pow

class Day17 : Day(2024, 17, "5,7,3,0" to 117440L) {
    override fun part1(input: List<String>): String {
        val computer = input.parseComputer()
        computer.execute()
        return computer.output.joinToString(",")
    }

    override fun part2(input: List<String>): Long {
        val computer = input.parseComputer()
        val ref = computer.program

        var goal: List<Long> = emptyList()
        var valueForA = 0L
        var targetLength = -1

        do {
            targetLength++
            valueForA *= 8
            goal = ref.subList(ref.lastIndex - targetLength, ref.size)
            var test = emptyList<Long>()
            while (goal != test) {
                val alt = computer.resetAndChangeA(valueForA)
                alt.execute()
                test = alt.output
                if (goal != test) {
                    valueForA++
                }
            }
        } while(goal != ref)

        return valueForA
    }
}

fun main() {
    Day17().execute().printResults()
}

private fun List<String>.parseComputer(): Computer {
    val registry = parseRegister()
    val program = this[4].parseProgram()
    return Computer(registry, program)
}

private fun List<String>.parseRegister(): Triple<Long, Long, Long> {
    val a = this[0].parseRegister()
    val b = this[1].parseRegister()
    val c = this[2].parseRegister()
    return Triple(a, b, c)
}

private fun String.parseRegister(): Long {
    val match = "Register [ABC]: ([0-9]+)".toRegex().find(this) ?: error("Invalid Registry")
    return match.groupValues[1].toLong()
}

private fun String.parseProgram(): List<Long> {
    return substring("Program: ".length).toLongList()
}

private fun Computer.execute(expection: List<Long>? = null) {
    while (instructionPointer in program.indices) {
        val code = program[instructionPointer]
        val operand = program[instructionPointer + 1]
        val instruction = Instruction.fromCode(code)
        instruction.execute(this, operand)
        if (instruction == Instruction.`out` && expection != null) {
            for (i in output.indices) {
                if (i in expection.indices && output[i] != expection[i]) {
                    return
                }
            }
        }
    }
}

private fun Computer.resetAndChangeA(a: Long): Computer {
    return copy(
        registry = initRegistry.copy(first = a),
        instructionPointer = 0,
        output = mutableListOf()
    )
}

private data class Computer(
    var registry: Triple<Long, Long, Long>,
    var program: List<Long>,

    var instructionPointer: Int = 0,

    val output: MutableList<Long> = mutableListOf(),
    var initRegistry: Triple<Long, Long, Long> = registry,
) {
    init {
        initRegistry = registry.copy()
    }
}

private fun Triple<Long, Long, Long>.valueOfOperand(operand: Long): Long {
    return when (operand) {
        0L, 1L, 2L, 3L -> operand
        4L -> first
        5L -> second
        6L -> third
        else -> error("Invalid operand")
    }
}

private enum class Instruction(
    val code: Long
) {
    adv(0) {
        override fun execute(computer: Computer, operand: Long) {
            val numerator = computer.registry.first
            val denominator = 2.0.pow(computer.registry.valueOfOperand(operand).toDouble())
            val result = (numerator / denominator).toLong()
            computer.registry = computer.registry.copy(first = result)
            computer.instructionPointer += 2
        }
    },
    bxl(1) {
        override fun execute(computer: Computer, operand: Long) {
            val left = computer.registry.second
            val right = operand
            val result = left xor right
            computer.registry = computer.registry.copy(second = result)
            computer.instructionPointer += 2
        }
    },
    bst(2) {
        override fun execute(computer: Computer, operand: Long) {
            val result = computer.registry.valueOfOperand(operand) % 8
            computer.registry = computer.registry.copy(second = result)
            computer.instructionPointer += 2
        }
    },
    jnz(3) {
        override fun execute(computer: Computer, operand: Long) {
            if (computer.registry.first != 0L) {
                computer.instructionPointer = operand.toInt()
            } else {
                computer.instructionPointer += 2
            }
        }
    },
    bxc(4) {
        override fun execute(computer: Computer, operand: Long) {
            val result = computer.registry.second xor computer.registry.third
            computer.registry = computer.registry.copy(second = result)
            computer.instructionPointer += 2
        }
    },
    `out`(5) {
        override fun execute(computer: Computer, operand: Long) {
            val op = computer.registry.valueOfOperand(operand) % 8
            computer.output.add(op)
            computer.instructionPointer += 2
        }
    },
    bdv(6) {
        override fun execute(computer: Computer, operand: Long) {
            val numerator = computer.registry.first
            val denominator = 2.0.pow(computer.registry.valueOfOperand(operand).toDouble())
            val result = (numerator / denominator).toLong()
            computer.registry = computer.registry.copy(second = result)
            computer.instructionPointer += 2
        }
    },
    cdv(7) {
        override fun execute(computer: Computer, operand: Long) {
            val numerator = computer.registry.first
            val denominator = 2.0.pow(computer.registry.valueOfOperand(operand).toDouble())
            val result = (numerator / denominator).toLong()
            computer.registry = computer.registry.copy(third = result)
            computer.instructionPointer += 2
        }
    };

    abstract fun execute(computer: Computer, operand: Long)

    companion object {
        fun fromCode(code: Long): Instruction {
            return entries.first { it.code == code }
        }
    }
}
