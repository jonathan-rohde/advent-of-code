package stats.downloader

import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.OffsetDateTime
import java.time.OffsetTime
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.deleteExisting
import kotlin.io.path.exists
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.readText
import kotlin.io.path.writeText

private val sessionCookie: String = System.getenv("AOC_SESSION_COOKIE")
    ?: error("Environment variable AOC_SESSION_COOKIE is not set. Please set it to your Advent of Code session cookie.")
private val leaderboardId: String = System.getenv("AOC_LEADERBOARD_ID")
    ?: error("Environment variable AOC_LEADERBOARD_ID is not set.")
internal val httpClient = OkHttpClient.Builder()
    .addInterceptor { chain ->
        chain.proceed(chain.request().newBuilder().addHeader("Cookie", "session=$sessionCookie").build())
    }
    .build()
internal val statsDataDownloader = StatsDataDownloader(httpClient)
internal val statsDataProvider = StatsDataProvider(statsDataDownloader)

internal val earliestYear = 2015
internal val latestYear = LocalDate.now().let {
    if (it.month == Month.DECEMBER) {
        it.year
    } else {
        it.year - 1
    }
}

class StatsDataProvider(
    private val statsDataDownloader: StatsDataDownloader
) {

    fun getStatsData(year: Int): String {
        val cacheDir = Path("data/stats")
        if (!cacheDir.exists()) {
            cacheDir.createDirectories()
        }
        val cacheFile = Path("$cacheDir/$year.json")
        if (cacheFile.exists()) {
            // check age
            val age = cacheFile.getLastModifiedTime()
            val distance = latestYear - year
            val useCache = age.toMillis() > cachableDate(distance)
            if (useCache) {
                return cacheFile.readText().trim()
            }
            cacheFile.deleteExisting()
        }

        val inputData = statsDataDownloader.downloadStatsData(year)
        cacheFile.createFile()
        cacheFile.writeText(inputData)
        return inputData
    }

    private fun cachableDate(distance: Int): Long =
        OffsetDateTime.now().withMinute(0).withSecond(0).withNano(0).minusMonths(distance.toLong())
            .toEpochSecond() * 1000
}

class StatsDataDownloader(private val httpClient: OkHttpClient) {

    fun downloadStatsData(year: Int): String {
        val request = Request.Builder()
            .url("https://adventofcode.com/$year/leaderboard/private/view/$leaderboardId.json")
            .build()
        return httpClient.newCall(request).execute().use {
            it.body.string().trim()
        }
    }
}
