package com.hcsc.de.claims.fileReaders

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.hcsc.de.claims.results.failsAnd
import com.hcsc.de.claims.results.succeedsAnd
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class JacksonJsonFileReaderTest {

    val jsonFileReader = JacksonJsonFileReader()

    @Test
    fun `it reads the test file into a JsonNode`() {

        jsonFileReader.read("src/test/resources/test.json") succeedsAnd { jsonNode ->

            val expectedNode: JsonNode = mapOf("hello" to "world").convert()

            assertThat(jsonNode).isEqualTo(expectedNode)
        }
    }

    @Test
    fun `if it cannot find the file, it returns a Failure`() {

        jsonFileReader.read("file-that-does-not-exist") failsAnd { message ->

            assertThat(message).isEqualTo("File at 'file-that-does-not-exist' was not found")
        }
    }

    @Test
    fun `if it cannot read the file in as a JsonNode, it returns a Failure`() {

        jsonFileReader.read("src/test/resources/test.txt") failsAnd { message ->

            assertThat(message).isEqualTo("File 'src/test/resources/test.txt' did not contain a valid JSON object")
        }
    }

    val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

    private inline fun <T, reified U : Any> T.convert() = objectMapper.convertValue(this, U::class.java)
}