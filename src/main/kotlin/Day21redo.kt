import utils.readInput
import utils.sign
import utils.testAndPrint
import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Long {
        movementCache.clear()
        return input.filter { it.isNotBlank() }
            .sumOf {
                it.extractNumber() * inputOperations(it, 2)
            }
    }

    fun part2(input: List<String>): Long {
        movementCache.clear()
        return input.filter { it.isNotBlank() }
            .sumOf {
                it.extractNumber() * inputOperations(it, 25)
            }
    }

    val testInput = readInput("Day21_test")
    part1(testInput).testAndPrint(126384L)
    part2(testInput).testAndPrint()

    val input = readInput("Day21")
    part1(input).testAndPrint()
    part2(input).testAndPrint()
}

private fun String.extractNumber(): Long = this.replace("[A-Z]".toRegex(), "").toLong(10)

private fun inputOperations(input: String, numberInAccessiblePads: Int): Long {
    return enterSequenceToPad(input, numberInAccessiblePads + 1) // human needs to do input as well
}

private fun enterSequenceToPad(code: String, indent: Int): Long {
    var from = 'A'
    var index = 0

    var length = 0L

    while (index < code.length) {
        val to = code[index]

        var cachedLength = movementCache["$from$to" to indent]
        if (cachedLength == null) {
            if (indent == 0) {
                cachedLength = 1
            } else {
                cachedLength = if (keypadMoves.containsKey(from to to)) {
                    // keypad
                    keypadMoves(from, to, indent)
                } else {
                    directionalMoves(from, to, indent)
                }.also {
                    movementCache["$from$to" to indent] = it
                }
            }
        }

        length += cachedLength
        from = to
        index++
    }

    return length
}

private fun keypadMoves(from: Char, to: Char, indent: Int): Long {
    return keypadMoves[from to to]!!.map { it + 'A' }
        .minOfOrNull { code ->
            enterSequenceToPad(code, indent - 1)
        }!!
}

private fun directionalMoves(from: Char, to: Char, indent: Int): Long {
    return directionalMoves[from to to]!!.map { it + 'A' }
        .minOfOrNull { code ->
            enterSequenceToPad(code, indent - 1)
        }!!
}

private val movementCache: MutableMap<Pair<String, Int>, Long> = mutableMapOf()

private val directional = listOf(
    " ^A",
    "<v>"
)

private val keypad = listOf(
    "789",
    "456",
    "123",
    " 0A"
)

private val keypadMoves = keypad.moves()
private val directionalMoves = directional.moves()

private fun List<String>.pathBetween(start: Pair<Int, Int>, end: Pair<Int, Int>): List<String> {
    val diffX = end.first - start.first
    val directionX = if (sign(diffX) == 1) '>' else '<'
    val diffY = end.second - start.second
    val directionY = if (sign(diffY) == 1) 'v' else '^'

    val buttons1 = mutableListOf<Char>()
    var path1 = ""
    (0 until abs(diffX)).forEach {
        buttons1.add(this[start.second][start.first + it * sign(diffX)])
        path1 += directionX
    }
    (0 until abs(diffY)).forEach {
        buttons1.add(this[start.second + it * sign(diffY)][end.first])
        path1 += directionY
    }

    val buttons2 = mutableListOf<Char>()
    var path2 = ""
    (0 until abs(diffY)).forEach {
        buttons2.add(this[start.second + it * sign(diffY)][start.first])
        path2 += directionY
    }
    (0 until abs(diffX)).forEach {
        buttons2.add(this[end.second][start.first + it * sign(diffX)])
        path2 += directionX
    }

    val possibleMoves = mutableListOf<String>()
    if (buttons1.none { it == ' ' }) possibleMoves.add(path1)
    if (buttons2.none { it == ' ' }) possibleMoves.add(path2)
    return possibleMoves.distinct()
}

private fun List<String>.moves(): Map<Pair<Char, Char>, List<String>> {
    val moves = mutableMapOf<Pair<Char, Char>, List<String>>()
    forEachIndexed { startY, startRow ->
        startRow.forEachIndexed { startX, startCode ->
            forEachIndexed { endY, endRow ->
                endRow.forEachIndexed { endX, endCode ->
                    if (startCode != ' ' && endCode != ' ') {
                        moves[startCode to endCode] = pathBetween(Pair(startX, startY), Pair(endX, endY))
                    }
                }
            }
        }
    }
    return moves
}