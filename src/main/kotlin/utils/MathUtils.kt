package utils

import kotlin.math.pow

fun sign(a: Int) = if (a < 0) -1 else if (a > 0) 1 else 0

fun List<Long>.lcm(): Long {
    val result = mutableMapOf<Int, Int>().withDefault { 0 }
    forEach {
        var remaining = it
        var prime = 2
        var count = 0
        while (remaining > 1) {
            if (remaining % prime == 0L) {
                remaining /= prime
                count++
            } else {
                result[prime] = maxOf(result.getValue(prime), count)
                prime++
                count = 0
            }
        }
        result[prime] = maxOf(result.getValue(prime), count)
    }
    return result.map { (k, v) -> k.toDouble().pow(v).toLong() }
        .reduce { acc, i -> acc * i }
}
