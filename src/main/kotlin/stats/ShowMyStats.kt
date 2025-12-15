package stats

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path
import kotlin.io.path.readText

@Serializable
private data class Stats(
    val event: Int,
    val members: Map<Int, Member>
)

@Serializable
private data class Member(
    val completion_day_level: Map<Int, DayLevel>,
    val last_star_ts: Long,
    val id: Int,
    val local_score: Int,
    val stars: Int,
    val name: String
)

private typealias DayLevel = Map<Int, StarCompletion>

@Serializable
private data class StarCompletion(
    val get_star_ts: Long,
    val star_index: Int
)

private val json = Json { ignoreUnknownKeys = true }

fun main() {
    val jsonData = readLineFromFile("src/main/resources/stats/2025.json")
    val obj = json.decodeFromString<Stats>(jsonData)
    obj.members.entries.first {
        it.value.name == "jonathan-rohde"
    }.let {
        println("Member: ${it.value.name}, Stars: ${it.value.stars}")
        it.value.completion_day_level.entries.sortedBy { it.key }.forEach { dayEntry ->
            val day = dayEntry.key
            val stars = dayEntry.value.entries.sortedBy { it.key }.joinToString("; ") { starEntry ->
                val ts = starEntry.value.get_star_ts.toZonedDateTime()
                "Star ${starEntry.key} at $ts"
            }
            val timeBetween = dayEntry.value.entries.sortedBy { it.key }.let {
                val duration = it.last().value.get_star_ts - it.first().value.get_star_ts
                " (Time between: ${Duration.ofSeconds(duration).toFormattedString()})"
            }
            println(" Day $day: $stars $timeBetween")
        }
    }
}

private fun Long.toZonedDateTime(): String {
    val localDate = LocalDateTime.ofEpochSecond(this, 0, ZoneOffset.UTC)
    return localDate.atZone(ZoneId.of("UTC"))
        .withZoneSameInstant(ZoneId.of("Europe/Berlin"))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}

private fun Duration.toFormattedString(): String {
    return String.format("%d:%02d:%02d",
        toHours(),
        toMinutesPart(),
        toSecondsPart())
}

private fun readLineFromFile(filename: String): String {
    return Path(filename).readText().trim()
}
