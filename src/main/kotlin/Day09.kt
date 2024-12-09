import utils.readInput
import utils.testAndPrint

fun main() {
    fun part1(input: List<String>): Long {
        return input.sumOf {
            val memory = it.createMemory()
            memory.fillGaps()
            memory.checksum()
        }
    }

    fun part2(input: List<String>): Long {
        return input.sumOf {
            val memory = it.createMemory()
            memory.fillGapsWithBlocks()
            memory.checksum()
        }
    }

    val testInput = readInput("Day09_test")
    part1(testInput).testAndPrint(1928L)
    part2(testInput).testAndPrint(2858L)

    // Read the input from the `src/DayXX.txt` file.
    val input = readInput("Day09")
    part1(input).testAndPrint()
    part2(input).testAndPrint()
}

private fun String.createMemory(): MutableList<Long?> {
    return map {
        it.toString().toLong()
    }.flatMapIndexed { index: Int, item: Long ->
        if (index % 2 == 0) {
            (1 .. item).map {
                index.toLong() / 2
            }
        } else {
            (1 .. item).map {
                null
            }
        }
    }.toMutableList()
}

private fun MutableList<Long?>.fillGaps() {
    var start = 0
    var end = nextNonNull(size - 1)
    while (start < end) {
        if (this[start] == null) {
            this[start] = this[end]
            this[end] = null
            end = nextNonNull(end)
        }
        start++
    }
}

private fun MutableList<Long?>.fillGapsWithBlocks() {
    val movedBlocks = mutableSetOf<Long>()
    var end = size - 1
    while (end >= 0) {
        val (movingBlockStart, movingBlockLen) = nextMovableBlock(end)
        if (movingBlockStart < 0) {
            end = movingBlockStart - 1
            continue
        }
        val targetBlockStart = nextFreeSpace(0, movingBlockLen)
        if (targetBlockStart == null) {
            // no free space, searching next
            end = movingBlockStart - 1
            continue
        }
        if (!movedBlocks.contains(this[movingBlockStart])
            && targetBlockStart < movingBlockStart) {
            // move block
            for (i in 0 until movingBlockLen) {
                this[targetBlockStart + i] = this[movingBlockStart + i]
                this[movingBlockStart + i] = null
            }
            movedBlocks.add(this[targetBlockStart]!!)
            end = movingBlockStart - 1
        } else {
            // block already moved, searching next
            end = movingBlockStart - 1
        }
    }
}

private fun MutableList<Long?>.nextMovableBlock(end: Int): Pair<Int, Int> {
    return nextNonNullBlock(end) ?: return -1 to -1
}

private fun MutableList<Long?>.nextFreeSpace(start: Int, len: Int): Int? {
    if (len == 0) {
        return null
    }
    var spaceStart = start
    while (spaceStart < size) {
        if (this[spaceStart] != null) {
            spaceStart++
            continue
        }
        val freeSpace = calculateFreeSpace(spaceStart)
        if (freeSpace >= len) {
            return spaceStart
        }
        spaceStart += freeSpace + 1
    }
    return null
}

private fun MutableList<Long?>.calculateFreeSpace(start: Int): Int {
    var spaceEnd = start
    while (spaceEnd < size && this[spaceEnd] == null) {
        spaceEnd++
    }
    return spaceEnd - start
}

private fun MutableList<Long?>.nextNonNull(end: Int): Int {
    var index = end
    while (this[index] == null) {
        index--
    }
    return index
}

private fun MutableList<Long?>.nextNonNullBlock(end: Int): Pair<Int, Int>? {
    var index = end
    while (index >= 0 && this[index] == null) {
        index--
    }
    if (index < 0) {
        return null
    }
    var len = 0
    val value = this[index]
    while(index >= 0 && this[index] != null && this[index] == value) {
        index--
        len++
    }
    if (index < 0) {
        return null
    }
    return index + 1 to len
}

private fun MutableList<Long?>.checksum(): Long {
    return mapIndexedNotNull { index, l -> l?.times(index.toLong()) }.sum()
}
