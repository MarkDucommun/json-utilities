package io.ducommun.jsonParsing

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.Test

class JsonParserTest {

    @Test
    fun `test`() {

        val result = ObjectMapper().registerKotlinModule().readValue("{\"a\":[\"a/*b*/c/*d//e\"]}", com.fasterxml.jackson.databind.JsonNode::class.java)

        val other = JsonParser().parse("{\"a\":[\"a/*b*/c/*d//e\"]}")
    }
}