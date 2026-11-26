package aoc.common.downloader

import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readLines
import kotlin.io.path.writeText

internal val answerDownloader = AnswerDownloader(httpClient)
internal val answerProvider = AnswerProvider(answerDownloader)

data class Answers(val part1: String?, val part2: String?)

class AnswerProvider(
    private val answerDownloader: AnswerDownloader
) {

    fun getAnswers(year: Int, day: Int): Answers {
        val cacheFile = Path("data/answers/$year/day${day.toString().padStart(2, '0')}.txt")
        if (cacheFile.exists()) {
            return cacheFile.readLines().toAnswers()
        }

        val page = answerDownloader.downloadAnswers(year, day)
        if (page.complete) {
            cacheFile.parent.createDirectories()
            cacheFile.writeText(page.answers.joinToString("\n"))
        }
        return page.answers.toAnswers()
    }

    private fun List<String>.toAnswers() = Answers(part1 = getOrNull(0), part2 = getOrNull(1))
}

data class AnswerPage(val answers: List<String>, val complete: Boolean)

class AnswerDownloader(private val httpClient: OkHttpClient) {

    private val answerPattern = Regex("""Your puzzle answer was <code>(.*?)</code>""")

    fun downloadAnswers(year: Int, day: Int): AnswerPage {
        val request = Request.Builder()
            .url("https://adventofcode.com/$year/day/$day")
            .build()
        val body = httpClient.newCall(request).execute().use { response ->
            val body = response.body.string()
            check(response.isSuccessful) {
                "Downloading puzzle page for $year day $day failed with HTTP ${response.code}"
            }
            body
        }
        check("class=\"user\"" in body) {
            "Puzzle page for $year day $day was served logged out; AOC_SESSION_COOKIE is invalid or expired"
        }
        return AnswerPage(
            answers = answerPattern.findAll(body).map { it.groupValues[1].decodeHtml() }.toList(),
            complete = "Both parts of this puzzle are complete" in body
        )
    }

    private fun String.decodeHtml(): String = this
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .replace("&amp;", "&")
}
