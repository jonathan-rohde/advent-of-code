package aoc.common.downloader

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
internal val httpClient = OkHttpClient.Builder()
    .addInterceptor { chain ->
        chain.proceed(chain.request().newBuilder().addHeader("Cookie", "session=$sessionCookie").build())
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
            return cacheFile.readText().trim().lines()
        }

        val inputData = inputDataDownloader.downloadInputData(year, day)
        cacheFile.createFile()
        cacheFile.writeText(inputData.joinToString("\n"))
        return inputData
    }
}

class InputDataDownloader(private val httpClient: OkHttpClient) {

    fun downloadInputData(year: Int, day: Int): List<String> {
        val request = Request.Builder()
            .url("https://adventofcode.com/$year/day/$day/input")
            .build()
        return httpClient.newCall(request).execute().use {
            it.body.string().trim().lines()
        }
    }
}
