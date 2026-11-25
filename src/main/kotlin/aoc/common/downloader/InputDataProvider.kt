package aoc.common.downloader

import aoc.common.errorPrintln
import aoc.common.toInputLines
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

private val sessionCookie: String by lazy {
    System.getenv("AOC_SESSION_COOKIE")?.takeIf { it.isNotBlank() }
        ?: error("Environment variable AOC_SESSION_COOKIE is not set. Please set it to your Advent of Code session cookie.")
}

private val userAgent: String = System.getenv("AOC_USER_AGENT")?.takeIf { it.isNotBlank() } ?: "advent-of-code-kotlin-runner"

internal val httpClient = OkHttpClient.Builder()
    .addInterceptor { chain ->
        chain.proceed(
            chain.request().newBuilder()
                .addHeader("Cookie", "session=$sessionCookie")
                .header("User-Agent", userAgent)
                .build()
        )
    }
    .build()

private val downloadInterval: Duration =
    (System.getenv("AOC_DOWNLOAD_INTERVAL_SECONDS")?.toLongOrNull()?.takeIf { it >= 0 } ?: 60L).seconds

internal val inputDataDownloader = InputDataDownloader(httpClient, downloadInterval)
internal val inputDataProvider = InputDataProvider(inputDataDownloader)


class InputDataProvider(
    private val inputDataDownloader: InputDataDownloader
) {

    fun getInputData(year: Int, day: Int): List<String> {
        val cacheDir = Path("data/inputs/$year")
        if (!cacheDir.exists()) {
            cacheDir.createDirectories()
        }
        val cacheFile = Path("data/inputs/$year/day${day.toString().padStart(2, '0')}.txt")
        if (cacheFile.exists()) {
            return cacheFile.readText().toInputLines()
        }

        val inputData = inputDataDownloader.downloadInputData(year, day)
        cacheFile.writeText(inputData)
        return inputData.toInputLines()
    }
}

class InputDataDownloader(
    private val httpClient: OkHttpClient,
    private val minInterval: Duration = Duration.ZERO,
) {
    private var lastDownload: TimeSource.Monotonic.ValueTimeMark? = null

    fun downloadInputData(year: Int, day: Int): String {
        throttle(year, day)
        val request = Request.Builder()
            .url("https://adventofcode.com/$year/day/$day/input")
            .build()
        return httpClient.newCall(request).execute().use { response ->
            val body = response.body.string()
            check(response.isSuccessful) {
                "Downloading input for $year day $day failed with HTTP ${response.code}: ${body.trim().take(200)}"
            }
            check(body.isNotBlank()) { "Downloaded input for $year day $day is empty" }
            body
        }
    }

    @Synchronized
    private fun throttle(year: Int, day: Int) {
        lastDownload?.let { last ->
            val wait = minInterval - last.elapsedNow()
            if (wait.isPositive()) {
                errorPrintln("Throttling: waiting ${wait.inWholeSeconds}s before downloading input for $year day $day")
                Thread.sleep(wait.inWholeMilliseconds)
            }
        }
        lastDownload = TimeSource.Monotonic.markNow()
    }
}
