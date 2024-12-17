import utils.readInput
import utils.testAndPrint
import utils.toIntList
import kotlin.math.pow

fun main() {
    fun part1(input: List<String>): String {
        val computer = input.parseComputer()
        computer.execute()
        return computer.output.joinToString(",")
    }

    fun part2(input: List<String>): Long {
        val computer = input.parseComputer()
        val refA = computer.registry.first

        val ref = computer.program.joinToString(",")

        var testCount = refA

        do {
            testCount++
            val alt = computer.resetAndChangeA(testCount)
            alt.execute()
            val test = alt.output.joinToString(",")
            println("$testCount: $test")
        } while (test != ref)

        return testCount.toLong()
    }

    val testInput = readInput("Day17_test")
    part1(testInput).testAndPrint("5,7,3,0")
    part2(testInput).testAndPrint(117440L)

    val input = readInput("Day17")
    part1(input).testAndPrint()
    part2(input).testAndPrint()
}

private fun List<String>.parseComputer(): Computer {
    val registry = parseRegister()
    val program = this[4].parseProgram()
    return Computer(registry, program)
}

private fun List<String>.parseRegister(): Triple<Int, Int, Int> {
    val a = this[0].parseRegister()
    val b = this[1].parseRegister()
    val c = this[2].parseRegister()
    return Triple(a, b, c)
}

private fun String.parseRegister(): Int {
    val match = "Register [ABC]: ([0-9]+)".toRegex().find(this) ?: error("Invalid Registry")
    return match.groupValues[1].toInt()
}

private fun String.parseProgram(): List<Int> {
    return substring("Program: ".length).toIntList()
}

private fun Computer.execute() {
    while (instructionPointer in program.indices) {
        val code = program[instructionPointer]
        val operand = program[instructionPointer + 1]
        val instruction = Instruction.fromCode(code)
        instruction.execute(this, operand)
    }
}

private fun Computer.resetAndChangeA(a: Int): Computer {
    return copy(
        registry = initRegistry.copy(first = a),
        instructionPointer = 0,
        output = mutableListOf()
    )
}

private data class Computer(
    var registry: Triple<Int, Int, Int>,
    val program: List<Int>,

    var instructionPointer: Int = 0,

    val output: MutableList<Int> = mutableListOf(),
    var initRegistry: Triple<Int, Int, Int> = registry,
) {
    init {
        initRegistry = registry.copy()
    }
}

private fun Triple<Int, Int, Int>.valueOfOperand(operand: Int): Int {
    return when (operand) {
        0, 1, 2, 3 -> operand
        4 -> first
        5 -> second
        6 -> third
        else -> error("Invalid operand")
    }
}

private enum class Instruction(
    val code: Int
) {
    adv(0) {
        override fun execute(computer: Computer, operand: Int) {
            val numerator = computer.registry.first
            val denominator = 2.0.pow(computer.registry.valueOfOperand(operand).toDouble())
            val result = (numerator / denominator).toInt()
            computer.registry = computer.registry.copy(first = result)
            computer.instructionPointer += 2
        }
    },
    bxl(1) {
        override fun execute(computer: Computer, operand: Int) {
            val left = computer.registry.second
            val right = operand
            val result = left xor right
            computer.registry = computer.registry.copy(second = result)
            computer.instructionPointer += 2
        }
    },
    bst(2) {
        override fun execute(computer: Computer, operand: Int) {
            val result = computer.registry.valueOfOperand(operand) % 8
            computer.registry = computer.registry.copy(second = result)
            computer.instructionPointer += 2
        }
    },
    jnz(3) {
        override fun execute(computer: Computer, operand: Int) {
            if (computer.registry.first != 0) {
                computer.instructionPointer = operand
            } else {
                computer.instructionPointer += 2
            }
        }
    },
    bxc(4) {
        override fun execute(computer: Computer, operand: Int) {
            val result = computer.registry.second xor computer.registry.third
            computer.registry = computer.registry.copy(second = result)
            computer.instructionPointer += 2
        }
    },
    `out`(5) {
        override fun execute(computer: Computer, operand: Int) {
            val op = computer.registry.valueOfOperand(operand) % 8
            computer.output.add(op)
            computer.instructionPointer += 2
        }
    },
    bdv(6) {
        override fun execute(computer: Computer, operand: Int) {
            val numerator = computer.registry.first
            val denominator = 2.0.pow(computer.registry.valueOfOperand(operand).toDouble())
            val result = (numerator / denominator).toInt()
            computer.registry = computer.registry.copy(second = result)
            computer.instructionPointer += 2
        }
    },
    cdv(7) {
        override fun execute(computer: Computer, operand: Int) {
            val numerator = computer.registry.first
            val denominator = 2.0.pow(computer.registry.valueOfOperand(operand).toDouble())
            val result = (numerator / denominator).toInt()
            computer.registry = computer.registry.copy(third = result)
            computer.instructionPointer += 2
        }
    };

    abstract fun execute(computer: Computer, operand: Int)

    companion object {
        fun fromCode(code: Int): Instruction {
            return entries.first { it.code == code }
        }
    }
}
