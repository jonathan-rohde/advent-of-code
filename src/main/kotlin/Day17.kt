import utils.readInput
import utils.testAndPrint
import utils.toLongList
import kotlin.math.pow

fun main() {
    fun part1(input: List<String>): String {
        val computer = input.parseComputer()
        computer.execute()
        return computer.output.joinToString(",")
    }

    fun part2Start(computer: Computer, start: Long): Long {
        val testCount = start
        var t = testCount
        var alt = computer.resetAndChangeA(t)
        alt.execute()
        while (alt.output.size < computer.program.size) {
            t *= 10
            alt = computer.resetAndChangeA(t)
            alt.execute()
        }
        return t + t/128
    }

    fun part2(input: List<String>, start: Long? = null): Long {
        val computer = input.parseComputer()

        val ref = computer.program.joinToString(",")

        val testCount = part2Start(computer, start ?: 256)
        generateSequence(testCount) { it + 1 }
            .forEach {
                val alt = computer.resetAndChangeA(it)
                alt.execute()
                val test = alt.output.joinToString(",")
                println("$it: $test")
                if (test == ref) {
                    return it
                }
            }
        return -1
    }

    val testInput = readInput("Day17_test")
    part1(testInput).testAndPrint("5,7,3,0")
    part2(testInput, 1).testAndPrint(117440L)

    val input = readInput("Day17")
    part1(input).testAndPrint()
    part2(input).testAndPrint()

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
