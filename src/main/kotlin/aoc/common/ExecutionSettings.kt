package aoc.common

object ExecutionSettings {
    var runs: Int = System.getProperty("aoc.runs")?.toIntOrNull() ?: 1
    var testOnly: Boolean = false
    var verify: Boolean = false
}
