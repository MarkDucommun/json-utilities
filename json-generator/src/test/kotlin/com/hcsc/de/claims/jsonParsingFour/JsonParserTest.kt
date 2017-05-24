package com.hcsc.de.claims.jsonParsingFour

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.Test

class JsonParserTest {

    @Test
    fun `test`() {


        var startTime = System.nanoTime()

        val result = ObjectMapper().registerKotlinModule().readValue("{\"a\":[\"a/*b*/c/*d//e\"]}", com.fasterxml.jackson.databind.JsonNode::class.java)

        println(System.nanoTime() - startTime)

        startTime = System.nanoTime()

        val other = JsonParser().parse("{\"a\":[\"a/*b*/c/*d//e\"]}")

        println(System.nanoTime() - startTime)

        println()
    }
}