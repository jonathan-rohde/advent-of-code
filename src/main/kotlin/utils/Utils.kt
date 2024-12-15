package utils

import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.time.TimedValue
import kotlin.time.measureTime

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/main/resources/$name.txt").readText().trim().lines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

fun TimedValue<Unit>.println() {
    println("Duration: $duration")
}

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun Any?.testAndPrint(checkValue: Any? = null) {
    println()
    if (checkValue != null) {
        check(this == checkValue) {
            "Check failed. Expected: $checkValue, actual: $this"
        }
    }
}

fun measured(task: Int, block: () -> Any?) {
    println("Task $task Duration: ${measureTime { block() }}")
}

/**
 * Parse string into list of integers
 */
fun String.toIntList(): List<Int> {
    if (this.isEmpty()) return emptyList()
    return this.split("\\s+|,".toRegex()).map { it.toInt() }
}

/**
 * Parse string into list of longs
 */
fun String.toLongList(): List<Long> {
    if (this.isEmpty()) return emptyList()
    return this.split("\\s+|,".toRegex()).map { it.toLong() }
}
