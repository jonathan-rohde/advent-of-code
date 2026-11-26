package aoc.common.downloader

import aoc.common.toInputLines
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

private val sessionCookie: String by lazy {
    System.getenv("AOC_SESSION_COOKIE")?.takeIf { it.isNotBlank() }
        ?: error("Environment variable AOC_SESSION_COOKIE is not set. Please set it to your Advent of Code session cookie.")
}

private val userAgent: String = System.getenv("AOC_USER_AGENT")?.takeIf { it.isNotBlank() } ?: "advent-of-code-kotlin-runner"

private const val MIN_REQUEST_INTERVAL_MS = 1000L
private var lastRequestAt = 0L

@Synchronized
private fun throttle() {
    val wait = lastRequestAt + MIN_REQUEST_INTERVAL_MS - System.currentTimeMillis()
    if (wait > 0) Thread.sleep(wait)
    lastRequestAt = System.currentTimeMillis()
}

internal val httpClient = OkHttpClient.Builder()
    .addInterceptor { chain ->
        throttle()
        chain.proceed(
            chain.request().newBuilder()
                .addHeader("Cookie", "session=$sessionCookie")
                .header("User-Agent", userAgent)
                .build()
        )
    }
    .build()
internal val inputDataDownloader = InputDataDownloader(httpClient)
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

class InputDataDownloader(private val httpClient: OkHttpClient) {

    fun downloadInputData(year: Int, day: Int): String {
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
}
