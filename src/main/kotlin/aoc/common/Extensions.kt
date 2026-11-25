package aoc.common

fun errorPrintln(message: String) {
    System.err.println(message)
}

fun String.toInputLines(): List<String> = trimStart('\r', '\n').trimEnd('\r', '\n').lines()
