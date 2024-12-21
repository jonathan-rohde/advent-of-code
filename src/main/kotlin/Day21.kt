import utils.println
import utils.readInput
import utils.testAndPrint
import java.util.ArrayDeque
import java.util.PriorityQueue

fun main() {
    fun part1(input: List<String>): Long {
        return input.filter { it.isNotBlank() }.sumOf {
            it.extractNumber() * tryInput(it)
        }
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }


//    var arm = Pair(2, 3)
//    var output = ""
//    val movements = listOf(
//        ArmMovement.LEFT,
//        ArmMovement.PRESS,
//        ArmMovement.UP,
//        ArmMovement.PRESS,
//        ArmMovement.RIGHT,
//        ArmMovement.UP,
//        ArmMovement.UP,
//        ArmMovement.PRESS,
//        ArmMovement.DOWN,
//        ArmMovement.DOWN,
//        ArmMovement.DOWN,
//        ArmMovement.PRESS
//    )
//
//    for (movement in movements) {
//        val t= pressureInput(movement, arm, output)
//        arm = t.first
//        output = t.second
//    }
//
//    println(output)

//    var arm1 = Pair(2, 3)
//    var arm2 = Pair(2, 0)
//    var output = ""
//    val movements = "v<<A>>^A<A>AvA<^AA>A<vAAA>^A"
//        .map {
//            when (it) {
//                '>' -> ArmMovement.RIGHT
//                '<' -> ArmMovement.LEFT
//                '^' -> ArmMovement.UP
//                'v' -> ArmMovement.DOWN
//                'A' -> ArmMovement.PRESS
//                else -> error("Unexpected character: $it")
//            }
//        }
//    for (movement in movements) {
//        var t = radiationInput(movement, arm2, arm1, output)
//        arm1 = t.second
//        arm2 = t.first
//        output = t.third
//    }
//    println(output)

//    var arm1 = Pair(2, 3)
//    var arm2 = Pair(2, 0)
//    var arm3 = Pair(2, 0)
//    var output = ""
//    val movements = "<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A"
//        .map {
//            when (it) {
//                '>' -> ArmMovement.RIGHT
//                '<' -> ArmMovement.LEFT
//                '^' -> ArmMovement.UP
//                'v' -> ArmMovement.DOWN
//                'A' -> ArmMovement.PRESS
//                else -> error("Unexpected character: $it")
//            }
//        }
//    for (movement in movements) {
//        var t = coldInput(movement, arm3, arm2, arm1, output)
//        arm1 = t.first.third
//        arm2 = t.first.second
//        arm3 = t.first.first
//        output = t.second
//    }
//    println(output)


    val testInput = readInput("Day21_test")
    part1(testInput).testAndPrint(126384L)
    part2(testInput).testAndPrint()

    val input = readInput("Day21")
    part1(input).testAndPrint()
    part2(input).testAndPrint()
}

private fun String.extractNumber(): Long = this.replace("[A-Z]".toRegex(), "").toLong(10)

private fun tryInput(input: String): Long {
    val queue = PriorityQueue<QueueData>(compareBy { it.length })
    val visited = mutableSetOf<CheckData>()
    ArmMovement.entries.forEach {
        queue.add(
            QueueData(
                cold = Pair(2, 0),
                radiation = Pair(2, 0),
                pressure = Pair(2, 3),
                output = "",
                length = 0L,
                movement = it,
            )
        )
    }

    while (queue.isNotEmpty()) {
        val data = queue.poll().also {
            it.println()
        }
        if (!visited.add(CheckData(
            cold = data.cold,
            radiation = data.radiation,
            pressure = data.pressure,
            output = data.output,
            movement = data.movement
        ))) continue
        if (data.output == input) {
            return data.length
        }
        val test = coldInput(data.movement, data.cold, data.radiation, data.pressure, data.output)
        val (cold, radiation, pressure) = test.first
        val a = test.second
        if (!cold.validPosition() || !radiation.validPosition() || !pressure.validKeypadPosition()) {
            continue
        }
        val movements = "<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A"
            .map {
                when (it) {
                    '>' -> ArmMovement.RIGHT
                    '<' -> ArmMovement.LEFT
                    '^' -> ArmMovement.UP
                    'v' -> ArmMovement.DOWN
                    'A' -> ArmMovement.PRESS
                    else -> error("Unexpected character: $it")
                }
            }
        if (input.startsWith(a)) {
            val moves = listOf(ArmMovement.PRESS, ArmMovement.UP, ArmMovement.DOWN, ArmMovement.LEFT, ArmMovement.RIGHT)
//                .filter {
//                    val list = data.previous + data.movement
//                    list.makeSense(it)
//                }
            moves.forEach {
                queue.add(
                    QueueData(
                        cold = cold,
                        radiation = radiation,
                        pressure = pressure,
                        output = a,
                        length = data.length + 1,
                        movement = it,
                        previous = if (data.movement == ArmMovement.PRESS) emptyList() else data.previous + data.movement,
                        typed = data.typed + data.movement
                    )
                )
            }
        }
//        }
    }

    return -1L
}

private fun coldInput(
    movement: ArmMovement,
    coldArmLoc: ColdPosition,
    radiationArmLoc: RadiationPosition,
    pressureArmLoc: PressurePosition,
    output: String
): Pair<Triple<ColdPosition, RadiationPosition, PressurePosition>, String> {
    if (movement == ArmMovement.PRESS) {
        val next =
            keyboard.get(coldArmLoc) ?: return Triple(coldArmLoc, radiationArmLoc, pressureArmLoc) to "invalid code"
        val radiation = radiationInput(
            movement = next,
            radiationArmLoc = radiationArmLoc,
            pressureArmLoc = pressureArmLoc,
            output = output
        )
        val positions = Triple(coldArmLoc, radiation.first, radiation.second)
        return positions to radiation.third
    }
    return Triple(movement(movement, coldArmLoc), radiationArmLoc, pressureArmLoc) to output
}

private fun radiationInput(
    movement: ArmMovement,
    radiationArmLoc: RadiationPosition,
    pressureArmLoc: PressurePosition,
    output: String
): Triple<RadiationPosition, PressurePosition, String> {
    if (movement == ArmMovement.PRESS) {
        val next = keyboard.get(radiationArmLoc) ?: return Triple(radiationArmLoc, pressureArmLoc, "invalid code")
        val pressure = pressureInput(movement = next, pressureArmLoc = pressureArmLoc, output = output)
        return Triple(radiationArmLoc, pressure.first, pressure.second)
    }
    return Triple(movement(movement, radiationArmLoc), pressureArmLoc, output)
}

private fun pressureInput(
    movement: ArmMovement,
    pressureArmLoc: PressurePosition,
    output: String
): Pair<PressurePosition, String> {
    if (movement == ArmMovement.PRESS) {
        return pressureArmLoc to output + keypad.get(pressureArmLoc).also {
            println("Access $it")
        }
    }
    return movement(movement, pressureArmLoc) to output
}

private enum class ArmMovement {
    UP, DOWN, LEFT, RIGHT, PRESS
}

private fun ArmMovement.opposite(): ArmMovement? {
    return when (this) {
        ArmMovement.UP -> ArmMovement.DOWN
        ArmMovement.DOWN -> ArmMovement.UP
        ArmMovement.LEFT -> ArmMovement.RIGHT
        ArmMovement.RIGHT -> ArmMovement.LEFT
        ArmMovement.PRESS -> null
    }
}

private val keypad = listOf(
    "789",
    "456",
    "123",
    " 0A"
)

private val keyboard = listOf(
    listOf(null, ArmMovement.UP, ArmMovement.PRESS),
    listOf(ArmMovement.LEFT, ArmMovement.DOWN, ArmMovement.RIGHT),
)

private fun movement(movement: ArmMovement, loc: Pair<Int, Int>): Pair<Int, Int> {
    return when (movement) {
        ArmMovement.UP -> loc.first to loc.second - 1
        ArmMovement.DOWN -> loc.first to loc.second + 1
        ArmMovement.LEFT -> loc.first - 1 to loc.second
        ArmMovement.RIGHT -> loc.first + 1 to loc.second
        ArmMovement.PRESS -> loc
    }
}

private fun List<String>.get(loc: Pair<Int, Int>): String {
    if (loc.second in indices && loc.first in this[loc.second].indices) {
        return this[loc.second][loc.first].toString()
    }
    return "this is invalid"
}

private fun List<List<ArmMovement?>>.get(loc: Pair<Int, Int>): ArmMovement? {
    if (loc.second in indices && loc.first in this[loc.second].indices) {
        return this[loc.second][loc.first]
    }
    return null
}

private typealias PressurePosition = Pair<Int, Int>
private typealias RadiationPosition = Pair<Int, Int>
private typealias ColdPosition = Pair<Int, Int>

private fun Pair<Int, Int>.validPosition(): Boolean {
    return second in keyboard.indices && first in keyboard[second].indices
            && keyboard[second][first] != null
}

private fun Pair<Int, Int>.validKeypadPosition(): Boolean {
    return second in keypad.indices && first in keypad[second].indices
            && keypad[second][first] != ' '
}

private data class QueueData(
    val cold: ColdPosition,
    val radiation: RadiationPosition,
    val pressure: PressurePosition,
    val output: String,
    val length: Long,
    val movement: ArmMovement,
    val previous: List<ArmMovement> = emptyList(),
    val typed: List<ArmMovement> = emptyList()
)

private data class CheckData(
    val cold: ColdPosition,
    val radiation: RadiationPosition,
    val pressure: PressurePosition,
    val output: String,
    val movement: ArmMovement
)

private fun List<ArmMovement>.makeSense(move: ArmMovement): Boolean {
    return when (move) {
        ArmMovement.UP -> none { it == ArmMovement.DOWN }
        ArmMovement.DOWN -> none { it == ArmMovement.UP }
        ArmMovement.LEFT -> none { it == ArmMovement.RIGHT }
        ArmMovement.RIGHT -> none { it == ArmMovement.LEFT }
        ArmMovement.PRESS -> true
    }
}