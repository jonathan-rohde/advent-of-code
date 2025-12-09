package aoc

import aoc.years.y2024.Year2024
import aoc.years.y2025.Year2025
import utils.toIntList

fun main(args: Array<String>) {
    var year = 2025
    var days = emptyList<Int>()
    var opaqueResults = true
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
    }

    when(year) {
        2024 -> Year2024().execute(days, opaqueResults)
        2025 -> Year2025().execute(days, opaqueResults)
    }
}
