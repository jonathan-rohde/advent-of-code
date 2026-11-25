package stats.downloader

import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDate
import java.time.Month
import java.time.OffsetDateTime
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteExisting
import kotlin.io.path.exists
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.readText
import kotlin.io.path.writeText

private val sessionCookie: String by lazy {
    System.getenv("AOC_SESSION_COOKIE")
        ?: error("Environment variable AOC_SESSION_COOKIE is not set. Please set it to your Advent of Code session cookie.")
}
private val leaderboardId: String by lazy {
    System.getenv("AOC_LEADERBOARD_ID")
        ?: error("Environment variable AOC_LEADERBOARD_ID is not set.")
}
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
            val cached = cacheFile.readText().trim()
            if (useCache && cached.looksLikeJsonObject()) {
                return cached
            }
            cacheFile.deleteExisting()
        }

        val inputData = statsDataDownloader.downloadStatsData(year)
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
        return httpClient.newCall(request).execute().use { response ->
            val body = response.body.string().trim()
            check(response.isSuccessful) {
                "Downloading stats for $year failed with HTTP ${response.code}: ${body.take(200)}"
            }
            check(body.looksLikeJsonObject()) {
                "Downloaded stats for $year are not JSON (expired session cookie or no leaderboard access?): ${body.take(200)}"
            }
            body
        }
    }
}

private fun String.looksLikeJsonObject(): Boolean = startsWith("{") && endsWith("}")
