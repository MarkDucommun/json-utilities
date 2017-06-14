package io.ducommun.jsonParsing

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.hcsc.de.claims.fileReading.RawByteStringFileReader
import com.hcsc.de.claims.results.*
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test
import java.io.File

class JsonParserTest {

    val fileReader = RawByteStringFileReader()

    val objectMapper = ObjectMapper().registerKotlinModule()

    val subject = JsonParser()

    @Test
    fun `it can parse and array with several nulls`() {

        subject.parse("[1,null,null,null,2]") succeedsAndShouldReturn ArrayNode(
                elements = listOf(
                        IntegerNode(1),
                        NullNode,
                        NullNode,
                        NullNode,
                        IntegerNode(2)
                ))
    }

    @Test
    fun `it fails a string with an unescaped control char`() {

        val string = fileReader.read("src/test/resources/test_files/n_string_unescaped_newline.json").get

        subject.parse(string) failsWithMessage "Invalid JSON - unescaped control character"
    }

    @Test
    fun `it runs all of the JSON test suite files`() {

        val results = File("src/test/resources/test_files").listFiles().map { testFile(it) }

        val notMatching = results.filterNot { it.matched || it.unclearFailure }

        assertThat(notMatching.size).isLessThanOrEqualTo(40)

        println("Currently not matching - ${notMatching.size}")

        println("Average time difference with Jackson: ${results.filter { it.matched && it.jacksonMatched }.map { it.timeDiff }.average()}ms")

        notMatching.forEach { outcome ->

            val jsonString = if (outcome.jsonString.length < 120) outcome.jsonString else "String too long"

            println("${outcome.fileName} -- $jsonString")
        }
    }

    fun testFile(file: File): OutcomeSummary {

        val jsonString = fileReader.read(file).get

        val result = time {
            try {

                subject.parse(jsonString)

            } catch (e: Error) {

                Failure<String, JsonNode>(e.message ?: "")
            }
        }

        val jacksonResult = time {
            wrapExternalLibraryUsageAsResult {

                objectMapper.readValue<com.fasterxml.jackson.databind.JsonNode>(jsonString)
            }
        }

        return OutcomeSummary(
                expected = when (file.name.first()) {
                    'y' -> Outcome.SUCCESS
                    'n' -> Outcome.FAILURE
                    'i' -> Outcome.UNCLEAR
                    else -> Outcome.UNKNOWN
                },
                fileName = file.name,
                jsonString = jsonString,
                result = result,
                jacksonResult = jacksonResult
        )
    }

    data class OutcomeSummary(
            val expected: Outcome,
            val fileName: String,
            val jsonString: String,
            val result: TimeAndResult<String, JsonNode>,
            val jacksonResult: TimeAndResult<String, com.fasterxml.jackson.databind.JsonNode>
    ) {

        val matched: Boolean = result.matched

        val jacksonMatched: Boolean = jacksonResult.matched

        val TimeAndResult<*, *>.matched: Boolean get() {

            return when (expected) {
                JsonParserTest.Outcome.SUCCESS -> this.result is Success
                JsonParserTest.Outcome.UNCLEAR -> true
                JsonParserTest.Outcome.UNKNOWN -> true
                JsonParserTest.Outcome.FAILURE -> this.result is Failure
            }
        }

        val unclearFailure: Boolean = expected == Outcome.UNCLEAR && result.result is Failure

        val fasterThanJackson: Boolean = result.elapsedTimeNanos < jacksonResult.elapsedTimeNanos

        val timeDiff: Double = jacksonResult.elapsedTimeMillis - result.elapsedTimeMillis
    }

    enum class Outcome { SUCCESS, UNCLEAR, UNKNOWN, FAILURE }
}