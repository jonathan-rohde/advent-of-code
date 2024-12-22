import utils.println
import utils.readInput
import utils.testAndPrint
import java.util.PriorityQueue

fun main() {
    fun part1(input: List<String>): Long {
        return input.filter { it.isNotBlank() }.sumOf {
            it.extractNumber() * tryInput(it, 2)
        }
    }

    fun part2(input: List<String>): Long {
        return input.filter { it.isNotBlank() }.sumOf {
            it.extractNumber() * tryInput(it, 25)
        }
    }

    val testInput = readInput("Day21_test")
//    part1(testInput).testAndPrint(126384L)
//    part2(testInput).testAndPrint()

    val input = readInput("Day21")
    part1(input).testAndPrint()
    part2(input).testAndPrint()
}

private fun String.extractNumber(): Long = this.replace("[A-Z]".toRegex(), "").toLong(10)

private fun tryInput(input: String, indentions: Int = 2): Long {
    robotCache.clear()
    val queue = PriorityQueue<QueueData2>(compareBy { it.length })
    val visited = mutableSetOf<CheckData2>()
    ArmMovement2.entries.forEach {
        queue.add(
            QueueData2(
                arms = ((0 until indentions).mapTo(mutableListOf()) { Pair(2, 0) } + Pair(2, 3)).toMutableList(),
                output = "",
                length = 0L,
                movement = it,
            )
        )
    }

    while (queue.isNotEmpty()) {
        val data = queue.poll()
        if (!visited.add(
                CheckData2(
                    arms = data.arms.toMutableList(),
                    output = data.output,
                    movement = data.movement
                )
            )
        ) {
            continue
        }
        if (data.output == input) {
            return data.length
        }

        val (arms, output) = robotMove(indentions, data.movement, data.arms.toMutableList(), data.output)
        if (!(arms.subList(0, arms.lastIndex).all { it.validPosition() } && arms[arms.lastIndex].validKeypadPosition())) continue
        if (input.startsWith(output)) {
            listOf(ArmMovement2.PRESS, ArmMovement2.UP, ArmMovement2.DOWN, ArmMovement2.LEFT, ArmMovement2.RIGHT)
                .forEach {
                    queue.add(
                        QueueData2(
                            arms = arms.toMutableList(),
                            output = output,
                            length = data.length + 1,
                            movement = it,
//                            typed = data.typed + data.movement
                        )
                    )
                }
        }
    }

    return -1L
}

private val robotCache: MutableMap<Triple<MutableList<ArmPosition>, ArmMovement2, Int>, Pair<MutableList<ArmPosition>, String>> = mutableMapOf()

private fun robotMove(
    indent: Int,
    movement: ArmMovement2,
    arms: MutableList<ArmPosition>,
    output: String
): Pair<MutableList<ArmPosition>, String> {
    val index = arms.lastIndex - indent
    if (robotCache.containsKey(Triple(arms, movement, indent))) {
        return robotCache[Triple(arms, movement, indent)]!!
    }
    if (index == arms.lastIndex) {
        // last robot
        if (movement == ArmMovement2.PRESS) {
            val out = keypad2.get(arms[index])
            return arms.toMutableList() to (output + out)
        }
        val newArms = arms.toMutableList()
        val newPosition = movement(movement, arms[index])
        newArms[index] = newPosition
        return newArms to output
    }

    if (movement == ArmMovement2.PRESS) {
        // forward press
        val nextMove = keyboard2.get(arms[index]) ?: return arms.toMutableList() to "output"
        val forward = robotMove(
            indent = indent - 1,
            movement = nextMove,
            arms = arms.toMutableList(),
            output = output,
        )
        return (forward.first to forward.second)
    }

    val newArms = arms.toMutableList()
    val newPosition = movement(movement, arms[index])
    newArms[index] = newPosition
    return (newArms to output)
}

//private fun tryInput(input: String, indentions: Int = 2): Long {
//    val queue = PriorityQueue<QueueData2>(compareBy { it.length })
//    val visited = mutableSetOf<CheckData2>()
//    val codeLengths = mutableMapOf<String, Long>()
//    ArmMovement2.entries.forEach {
//        queue.add(
//            QueueData2(
//                arms = ((0 until indentions).mapTo(mutableListOf()) { Pair(2, 0) } + Pair(2, 3)).toMutableList(),
//                output = "",
//                length = 0L,
//                movement = it,
//            )
//        )
//    }
//
//    while (queue.isNotEmpty()) {
//        val data = queue.poll()
//        if (!visited.add(
//                CheckData2(
//                    arms = data.arms.toMutableList(),
//                    output = data.output,
//                    movement = data.movement
//                )
//            )
//        ) {
//            continue
//        }
//        if (data.output == input) {
//            return data.length
//        }
//
//        val (arms, output) = robotMove(indentions, data.movement, data.arms.toMutableList(), data.output)
//        if (!(arms.subList(0, arms.lastIndex).all { it.validPosition() } && arms[arms.lastIndex].validKeypadPosition())) continue
//        if (input.startsWith(output)) {
//            listOf(ArmMovement2.PRESS, ArmMovement2.UP, ArmMovement2.DOWN, ArmMovement2.LEFT, ArmMovement2.RIGHT)
//                .forEach {
//                    queue.add(
//                        QueueData2(
//                            arms = arms.toMutableList(),
//                            output = output,
//                            length = data.length + 1,
//                            movement = it,
//                            typed = data.typed + data.movement
//                        )
//                    )
//                }
//        }
//    }
//
//    return -1L
//}
//
//private fun robotMove(
//    indent: Int,
//    movement: ArmMovement2,
//    arms: MutableList<ArmPosition>,
//    output: String
//): Pair<MutableList<ArmPosition>, String> {
//    val index = arms.lastIndex - indent
//    if (index == arms.lastIndex) {
//        // last robot
//        if (movement == ArmMovement2.PRESS) {
//            val out = keypad2.get(arms[index])
//            return arms.toMutableList() to (output + out)
//        }
//        val newArms = arms.toMutableList()
//        val newPosition = movement(movement, arms[index])
//        newArms[index] = newPosition
//        return newArms to output
//    }
//    if (movement == ArmMovement2.PRESS) {
//        // forward press
//        val nextMove = keyboard2.get(arms[index]) ?: return arms.toMutableList() to "output"
//        val forward = robotMove(
//            indent = indent - 1,
//            movement = nextMove,
//            arms = arms.toMutableList(),
//            output = output,
//        )
//        return forward.first to forward.second
//    }
//
//    val newArms = arms.toMutableList()
//    val newPosition = movement(movement, arms[index])
//    newArms[index] = newPosition
//    return newArms to output
//}


private enum class ArmMovement2 {
    UP, DOWN, LEFT, RIGHT, PRESS
}

private val keypad2 = listOf(
    "789",
    "456",
    "123",
    " 0A"
)

private val keyboard2 = listOf(
    listOf(null, ArmMovement2.UP, ArmMovement2.PRESS),
    listOf(ArmMovement2.LEFT, ArmMovement2.DOWN, ArmMovement2.RIGHT),
)

private fun movement(movement: ArmMovement2, loc: Pair<Int, Int>): Pair<Int, Int> {
    return when (movement) {
        ArmMovement2.UP -> loc.first to loc.second - 1
        ArmMovement2.DOWN -> loc.first to loc.second + 1
        ArmMovement2.LEFT -> loc.first - 1 to loc.second
        ArmMovement2.RIGHT -> loc.first + 1 to loc.second
        ArmMovement2.PRESS -> loc
    }
}

private fun List<String>.get(loc: Pair<Int, Int>): String {
    if (loc.second in indices && loc.first in this[loc.second].indices) {
        return this[loc.second][loc.first].toString()
    }
    return "this is invalid"
}

private fun List<List<ArmMovement2?>>.get(loc: Pair<Int, Int>): ArmMovement2? {
    if (loc.second in indices && loc.first in this[loc.second].indices) {
        return this[loc.second][loc.first]
    }
    return null
}

private typealias ArmPosition = Pair<Int, Int>

private fun Pair<Int, Int>.validPosition(): Boolean {
    return second in keyboard2.indices && first in keyboard2[second].indices
            && keyboard2[second][first] != null
}

private fun Pair<Int, Int>.validKeypadPosition(): Boolean {
    return second in keypad2.indices && first in keypad2[second].indices
            && keypad2[second][first] != ' '
}

private data class QueueData2(
    val arms: MutableList<ArmPosition>,
    val output: String,
    val length: Long,
    val movement: ArmMovement2,
    val typed: List<ArmMovement2> = emptyList()
)

private data class CheckData2(
    val arms: MutableList<ArmPosition>,
    val output: String,
    val movement: ArmMovement2
)