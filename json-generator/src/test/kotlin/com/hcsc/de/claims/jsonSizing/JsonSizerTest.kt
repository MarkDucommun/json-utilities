package com.hcsc.de.claims.jsonSizing

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.hcsc.de.claims.results.succeedsAnd
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class JsonSizerTest {

    val jsonSizer = JsonSizer()

    @Test
    fun `it returns the number of bytes in the JSON object and all of its child nodes number of bytes`() {

        val jsonString: String = mapOf("top" to mapOf("A" to "XXXX", "B" to "XXXX")).asJson

        jsonSizer.calculateSize(jsonString) succeedsAnd { rootNode ->

            val sizeDescription = rootNode.findChild("top")

            assertThat(sizeDescription).isEqualTo(JsonSizeObject(
                    name = "top",
                    size = 23,
                    children = listOf(
                            JsonSizeLeafNode(name = "A", size = 6),
                            JsonSizeLeafNode(name = "B", size = 6)
                    )
            ))
        }
    }

    @Test
    fun `it can uses floor for converting doubles that are odd`() {

        val jsonString: String = mapOf("top" to mapOf("A" to "XXXX", "B" to "XXXXXXX")).asJson

        jsonSizer.calculateSize(jsonString) succeedsAnd { rootNode ->

            val sizeDescription = rootNode.findChild("top")

            assertThat(sizeDescription).isEqualTo(JsonSizeObject(
                    name = "top",
                    size = 26,
                    children = listOf(
                            JsonSizeLeafNode(name = "A", size = 6),
                            JsonSizeLeafNode(name = "B", size = 9)
                    )
            ))
        }
    }

    @Test
    fun `it can uses ceiling for converting doubles that are even`() {

        val jsonString: String = mapOf("top" to mapOf("A" to "XXXXX", "B" to "XXXXXXXX")).asJson

        jsonSizer.calculateSize(jsonString) succeedsAnd { rootNode ->

            val sizeDescription = rootNode.findChild("top")

            assertThat(sizeDescription).isEqualTo(JsonSizeObject(
                    name = "top",
                    size = 28,
                    children = listOf(
                            JsonSizeLeafNode(name = "A", size = 7),
                            JsonSizeLeafNode(name = "B", size = 10)
                    )
            ))
        }
    }

    @Test
    fun `it can size JSON objects with arrays`() {

        val jsonString: String = mapOf("top" to listOf("XXXX", "XXXXXXX")).asJson

        jsonSizer.calculateSize(jsonString) succeedsAnd { rootNode ->

            val sizeDescription = rootNode.findChild("top")

            assertThat(sizeDescription).isEqualTo(JsonSizeArray(
                    name = "top",
                    size = 18,
                    children = listOf(
                            JsonSizeLeafNode(name = "0", size = 6),
                            JsonSizeLeafNode(name = "1", size = 9)
                    )
            ))
        }
    }

    @Test
    fun `it can size JSON objects with arrays of complex objects`() {

        val jsonString: String = mapOf("top" to listOf(
                mapOf("A" to "XXXX", "B" to "XXXXX"),
                mapOf("C" to "XXXXXX", "D" to "XXXXXXXX"))).asJson

        jsonSizer.calculateSize(jsonString) succeedsAnd { rootNode ->

            val sizeDescription = rootNode.findChild("top")

            assertThat(sizeDescription).isEqualToComparingFieldByFieldRecursively(JsonSizeArray(
                    name = "top",
                    size = 56,
                    children = listOf(
                            JsonSizeObject(
                                    name = "0",
                                    size = 24,
                                    children = listOf(
                                            JsonSizeLeafNode(name = "A", size = 6),
                                            JsonSizeLeafNode(name = "B", size = 7)
                                    )
                            ),
                            JsonSizeObject(
                                    name = "1",
                                    size = 29,
                                    children = listOf(
                                            JsonSizeLeafNode(name = "C", size = 8),
                                            JsonSizeLeafNode(name = "D", size = 10)
                                    )
                            )
                    )
            ))
        }
    }

    @Test
    fun `it parses a null value as a JsonSizeEmpty`() {

        val jsonString: String = mapOf("key" to null).asJson

        jsonSizer.calculateSize(jsonString) succeedsAnd { rootNode ->
            assertThat(rootNode.findChild("key")).isEqualTo(JsonSizeEmpty(name = "key"))
        }
    }

    @Test
    fun `it parses empty string as a JsonSizeEmpty`() {
        val jsonString: String = ""

        jsonSizer.calculateSize(jsonString) succeedsAnd { rootNode ->
            assertThat(rootNode).isEqualTo(JsonSizeEmpty(name = ""))
        }
    }

    private fun JsonSizeNode.findChild(name: String): JsonSizeNode {
        return when (this) {
            is JsonSizeObject -> children.find { it.name == name } ?: throw RuntimeException("Node was not found")
            else -> throw RuntimeException("Root was the wrong type")
        }
    }

    val objectMapper = ObjectMapper().registerKotlinModule()

    private val Any?.asJson get()  = objectMapper.writeValueAsString(this)


}
