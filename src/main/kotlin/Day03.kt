import utils.println
import utils.readInput

fun main() {
    fun part1(input: List<String>): Int = input.joinToString(" ").calculate(true)

    fun part2(input: List<String>): Int = input.joinToString(" ").calculate(false)

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 161)
    check(part2(readInput("Day03_test2")) == 48)

    // Read the input from the `src/DayXX.txt` file.
    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}

private fun String.calculate(enabled: Boolean = true): Int {
    val pattern = """
        mul\((\d+),(\d+)\)
    """.trimIndent().toRegex()
    val matches = pattern.findAll(this)

    return matches.map {
        if (enabled) {
            return@map it.groupValues[1].toInt() * it.groupValues[2].toInt()
        }
        val prefix = this.substring(0, it.range.first)
        val enable = prefix.lastIndexOf("do()")
        val disable = prefix.lastIndexOf("don't()")
        if (disable == -1) {
            it.groupValues[1].toInt() * it.groupValues[2].toInt()
        } else {
            if (enable == -1 || enable < disable) {
                0
            } else {
                it.groupValues[1].toInt() * it.groupValues[2].toInt()
            }
        }
    }.sum()
}
