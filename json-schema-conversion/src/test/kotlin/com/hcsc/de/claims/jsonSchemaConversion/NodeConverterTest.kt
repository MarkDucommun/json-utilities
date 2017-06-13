package com.hcsc.de.claims.jsonSchemaConversion

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.assertj.core.api.KotlinAssertions
import org.junit.Test

class NodeConverterTest {

    val nodeConverter = NodeConverter()

    @Test
    fun `converts the simplest objects - string`() {
        val testNode: JsonNode = mapOf(
                "type" to "string",
                "maxLength" to 30
        ).convert()

        val schema = nodeConverter.convert(testNode)

        KotlinAssertions.assertThat(schema).isEqualTo(Text(maxLength = 30))
    }

    @Test
    fun `converts the simplest objects - date`() {
        val testNode: JsonNode = mapOf(
                "type" to "string",
                "format" to "date"
        ).convert()

        val schema = nodeConverter.convert(testNode)

        KotlinAssertions.assertThat(schema).isEqualTo(Date)
    }

    @Test
    fun `converts the simplest objects - date-time`() {
        val testNode: JsonNode = mapOf(
                "type" to "string",
                "format" to "date-time"
        ).convert()

        val schema = nodeConverter.convert(testNode)

        KotlinAssertions.assertThat(schema).isEqualTo(DateTime)
    }

    @Test
    fun `converts the simplest objects - number`() {
        val testNode: JsonNode = mapOf(
                "type" to "number"
        ).convert()

        val schema = nodeConverter.convert(testNode)

        KotlinAssertions.assertThat(schema).isEqualTo(Number)
    }

    @Test
    fun `converts the simplest objects - integer`() {
        val testNode: JsonNode = mapOf(
                "type" to "integer"
        ).convert()

        val schema = nodeConverter.convert(testNode)

        KotlinAssertions.assertThat(schema).isEqualTo(Integer)
    }

    @Test
    fun `converts a simple object`() {

        val testNode: JsonNode = mapOf(
                "type" to "object",
                "properties" to mapOf(
                        "fieldA" to mapOf("type" to "string", "maxLength" to 30),
                        "fieldB" to mapOf("type" to "string", "maxLength" to 15)
                )
        ).convert()

        val schema = nodeConverter.convert(testNode)

        KotlinAssertions.assertThat(schema).isEqualTo(ComplexObject(
                properties = listOf(
                        SchemaObject(name = "fieldA", detail = Text(maxLength = 30)),
                        SchemaObject(name = "fieldB", detail = Text(maxLength = 15))
                )
        ))
    }

    @Test
    fun `it converts a simple array`() {
        val testNode: JsonNode = mapOf(
                "type" to "array",
                "items" to mapOf(
                        "type" to "string",
                        "maxLength" to 15
                )
        ).convert()

        val schema = nodeConverter.convert(testNode)

        KotlinAssertions.assertThat(schema).isEqualTo(ArrayDetail(
                itemType = Text(maxLength = 15),
                maxItems = null
        ))
    }

    @Test
    fun `it converts a simple array with maxItems`() {
        val testNode: JsonNode = mapOf(
                "type" to "array",
                "items" to mapOf(
                        "type" to "string",
                        "maxLength" to 15
                ),
                "maxItems" to 100
        ).convert()

        val schema = nodeConverter.convert(testNode)

        KotlinAssertions.assertThat(schema).isEqualTo(ArrayDetail(
                itemType = Text(maxLength = 15),
                maxItems = 100
        ))
    }

    @Test
    fun `it converts a more complex array`() {
        val testNode: JsonNode = mapOf(
                "type" to "array",
                "items" to mapOf(
                        "type" to "object",
                        "properties" to mapOf(
                                "fieldA" to mapOf("type" to "string", "maxLength" to 30),
                                "fieldB" to mapOf("type" to "string", "maxLength" to 15)
                        )
                ),
                "maxItems" to 100
        ).convert()

        val schema = nodeConverter.convert(testNode)

        KotlinAssertions.assertThat(schema).isEqualTo(ArrayDetail(
                itemType = ComplexObject(
                        properties = listOf(
                                SchemaObject(name = "fieldA", detail = Text(maxLength = 30)),
                                SchemaObject(name = "fieldB", detail = Text(maxLength = 15))
                        )
                ),
                maxItems = 100
        ))
    }

    @Test
    fun `it converts a Reference`() {
        val testNode: JsonNode = mapOf("\$ref" to "definition").convert()

        val schema = nodeConverter.convert(testNode)

        KotlinAssertions.assertThat(schema).isEqualTo(Reference(type = "definition"))
    }

    @Test
    fun `it converts a OneOf`() {
        val testNode: JsonNode = mapOf(
                "oneOf" to listOf(
                        mapOf("\$ref" to "definition"),
                        mapOf("type" to "string", "maxLength" to 15)
                )
        ).convert()

        val schema = nodeConverter.convert(testNode)

        KotlinAssertions.assertThat(schema).isEqualTo(OneOf(
                list = listOf(
                        Reference(type = "definition"),
                        Text(maxLength = 15)
                )
        ))
    }

    val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

    private inline fun <T, reified U : Any> T.convert() = objectMapper.convertValue(this, U::class.java)
}