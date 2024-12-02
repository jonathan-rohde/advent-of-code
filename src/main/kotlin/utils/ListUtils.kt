package utils


fun List<Int>.isSorted(strict: Boolean = false): Boolean {
    val groups = this.windowed(2).map {
        sign(it[0] - it[1])
    }.groupBy { it }

    return if (strict) {
        !groups.keys.contains(0) && groups.keys.contains(-1).xor(groups.keys.contains(1))
    } else {
        groups.keys.contains(-1).xor(groups.keys.contains(1))
    }
}