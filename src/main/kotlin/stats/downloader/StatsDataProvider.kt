package stats.downloader

import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists
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
            return cacheFile.readText().trim()
        }

        val inputData = statsDataDownloader.downloadStatsData(year)
        cacheFile.createFile()
        cacheFile.writeText(inputData)
        return inputData
    }
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
