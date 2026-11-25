package aoc

import aoc.common.ExecutionSettings
import aoc.common.Year
import aoc.common.errorPrintln
import aoc.years.y2015.Year2015
import aoc.years.y2016.Year2016
import aoc.years.y2023.Year2023
import aoc.years.y2024.Year2024
import aoc.years.y2025.Year2025
import utils.toIntList
import java.time.LocalDate
import java.time.Month
import kotlin.system.exitProcess

private val years: Map<Int, () -> Year> = mapOf(
    2015 to ::Year2015,
    2016 to ::Year2016,
    2023 to ::Year2023,
    2024 to ::Year2024,
    2025 to ::Year2025,
)

private val usage = """
    Usage: Main [options]
      --year <year>     year to run (default: latest AoC year, supported: ${years.keys.joinToString()})
      --day <days>      only run these days, e.g. "1,2,5" (default: all)
      --runs <n>        run each part n times and show min/avg/median/max (default: 1)
      --show-results    show test failure details
      --help            show this help
""".trimIndent()

private data class Options(
    val year: Int = latestYear(),
    val days: List<Int> = emptyList(),
    val runs: Int = ExecutionSettings.runs,
    val opaqueResults: Boolean = true,
    val help: Boolean = false,
)

fun main(args: Array<String>) {
    val options = runCatching { parseOptions(args) }.getOrElse { error ->
        errorPrintln("${error.message}\n\n$usage")
        exitProcess(1)
    }
    if (options.help) {
        println(usage)
        return
    }

    val year = years[options.year] ?: run {
        errorPrintln("Year ${options.year} is not supported. Supported: ${years.keys.joinToString()}")
        exitProcess(1)
    }

    ExecutionSettings.runs = options.runs
    year().execute(options.days, options.opaqueResults)
}

private fun parseOptions(args: Array<String>): Options {
    var options = Options()
    val iterator = args.iterator()
    while (iterator.hasNext()) {
        options = when (val arg = iterator.next()) {
            "--year" -> options.copy(year = iterator.intValue(arg))
            "--day" -> options.copy(days = iterator.daysValue(arg))
            "--runs" -> options.copy(runs = iterator.intValue(arg).also { require(it > 0) { "--runs must be at least 1" } })
            "--show-results" -> options.copy(opaqueResults = false)
            "--help", "-h" -> options.copy(help = true)
            else -> throw IllegalArgumentException("Unknown argument: $arg")
        }
    }
    return options
}

private fun Iterator<String>.value(option: String): String {
    require(hasNext()) { "$option needs a value" }
    return next()
}

private fun Iterator<String>.intValue(option: String): Int {
    val value = value(option)
    return requireNotNull(value.toIntOrNull()) { "$option needs a number, got \"$value\"" }
}

private fun Iterator<String>.daysValue(option: String): List<Int> {
    val value = value(option)
    return runCatching { value.toIntList() }.getOrElse {
        throw IllegalArgumentException("$option needs a list of days like \"1,2,5\", got \"$value\"")
    }
}

private fun latestYear(): Int = LocalDate.now().let { if (it.month == Month.DECEMBER) it.year else it.year - 1 }
