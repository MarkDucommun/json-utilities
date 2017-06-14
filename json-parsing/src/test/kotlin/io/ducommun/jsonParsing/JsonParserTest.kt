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
    fun `it fails a literal separated by a whitespace`() {

        subject.parse("[1 000.0]") failsWithMessage "Invalid JSON - improperly formatted array"
    }

    @Test
    fun `it fails a comma trailing an object element followed by an object close`() {

        subject.parse("{\"id\":0,}") failsWithMessage "Invalid JSON - object close cannot immediately follow object comma"
    }

    @Test
    fun `it fails blah`() {

        subject.parse("1]") failsWithMessage "?"
    }

    @Test
    fun `it runs all of the JSON test suite files`() {

        val results = File("src/test/resources/test_files").listFiles().map { testFile(it) }

        val notMatching = results.filterNot { it.matched }

        val throwingErrors = results.filter { it.errorThrown }

        assertThat(notMatching.size).isLessThanOrEqualTo(25)
        assertThat(throwingErrors.size).isLessThanOrEqualTo(5)

        if (true) {

            val jacksonNotMatching = results.filterNot { it.jacksonMatchedExpected }

            val notMatchingJackson = results.filterNot { it.matchesJackson }

            println("Currently not matching - ${notMatching.size}")

            println("Currently throwing errors - ${throwingErrors.size}")

            println("Jackson currently not matching - ${jacksonNotMatching.size}")

            println("Currently not matching Jackson - ${notMatchingJackson.size}")

            println("Average time difference with Jackson: ${results.filter { it.matched && it.jacksonMatchedExpected }.map { it.timeDiff }.average()}ms")

            println("\nFiles Not Matching")
            notMatching.forEach { (_, fileName, jsonString) ->

                val jsonString = if (jsonString.length < 120) jsonString else "String too long"

                println("$fileName -- $jsonString")
            }

            println("\nFiles Throwing Error")
            throwingErrors.forEach { (_, fileName, jsonString) ->

                val jsonString = if (jsonString.length < 120) jsonString else "String too long"

                println("$fileName -- $jsonString")
            }
        }
    }

    fun testFile(file: File): OutcomeSummary {

        val jsonString = fileReader.read(file).get

        var errorThrown = false

        val result = time {
            try {

                subject.parse(jsonString)

            } catch (e: Error) {

                errorThrown = true

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
                jacksonResult = jacksonResult,
                errorThrown = errorThrown
        )
    }

    data class OutcomeSummary(
            val expected: Outcome,
            val fileName: String,
            val jsonString: String,
            val result: TimeAndResult<String, JsonNode>,
            val jacksonResult: TimeAndResult<String, com.fasterxml.jackson.databind.JsonNode>,
            val errorThrown: Boolean
    ) {

        val matched: Boolean = result.matched

        val jacksonMatchedExpected: Boolean = jacksonResult.matched

        val TimeAndResult<*, *>.matched: Boolean get() {

            return when (expected) {
                JsonParserTest.Outcome.SUCCESS -> this.result is Success
                JsonParserTest.Outcome.UNCLEAR -> true
                JsonParserTest.Outcome.UNKNOWN -> true
                JsonParserTest.Outcome.FAILURE -> this.result is Failure
            }
        }

        val matchesJackson: Boolean =
                result.result is Success && jacksonResult.result is Success ||
                        result.result is Failure && jacksonResult.result is Failure

        val unclearFailure: Boolean = expected == Outcome.UNCLEAR && result.result is Failure

        val fasterThanJackson: Boolean = result.elapsedTimeNanos < jacksonResult.elapsedTimeNanos

        val timeDiff: Double = jacksonResult.elapsedTimeMillis - result.elapsedTimeMillis
    }

    enum class Outcome { SUCCESS, UNCLEAR, UNKNOWN, FAILURE }
}