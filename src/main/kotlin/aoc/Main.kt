package aoc

import aoc.years.y2024.Year2024
import utils.toIntList

fun main(args: Array<String>) {
    var year = 2024
    var days = emptyList<Int>()
    var opaqueResults = true
    var runs = 1
    for (i in 0 .. args.lastIndex) {
        if (args[i] == "--show-results") {
            opaqueResults = false
        }
        if (args[i] == "--year") {
            year = args[i + 1].toInt()
        }
        if (args[i] == "--day") {
            days = args[i + 1].toIntList()
        }
        if (args[i] == "--runs") {
            runs = args[i + 1].toInt()
        }
    }

    when(year) {
        2024 -> Year2024().execute(days, opaqueResults, runs)
    }
}
