package com.hcsc.de.claims.jsonSizing

import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.hcsc.de.claims.collection.helpers.ifNotEmptyOtherwiseNull
import com.hcsc.de.claims.kotlinJacksonHelpers.fieldNames
import com.hcsc.de.claims.math.helpers.ceilingOnEven

class JsonSizer {

    val objectMapper = com.fasterxml.jackson.databind.ObjectMapper().registerKotlinModule()

    fun calculateSize(string: String): com.hcsc.de.claims.results.Result<String, JsonSizeNode> {

        return com.hcsc.de.claims.results.Success(content = string.understandJsonSize())
    }

    fun String.understandJsonSize(): JsonSizeNode {

        return if (this.isNotEmpty()) {
            val jsonNode = objectMapper.readValue<com.fasterxml.jackson.databind.JsonNode>(this)

            val children = jsonNode.fieldNames.map { fieldName -> jsonNode[fieldName].understandSize(name = fieldName) }

            JsonSizeObject(
                    name = "root",
                    size = children.map { it.size }.sum(),
                    children = children

            )
        } else {
            JsonSizeEmpty(name = "")
        }
    }

    fun com.fasterxml.jackson.databind.JsonNode.understandSize(name: String): JsonSizeNode {
        println()
        return when {
            this.isObject -> generateJsonSizeObject(
                    name = name,
                    size = this.writeAsString().length,
                    children = this.fieldNames.map { name ->
                        this[name].understandSize(name = name)
                    }
            )
            this.isArray -> generateJsonSizeArray(
                    name = name,
                    size = this.writeAsString().length,
                    children = this.mapIndexed { index, node ->
                        node.understandSize(name = index.toString())
                    }
            )
            this.isNull -> generateJsonEmpty(name = name)
            else -> JsonSizeLeafNode(name = name, size = this.writeAsString().length)
        }
    }

    private fun <T> T.writeAsString() = objectMapper.writeValueAsString(this)

    fun List<JsonSizeNode>.averageSize() = map(JsonSizeNode::size)
            .ifNotEmptyOtherwiseNull { average() }
            ?.ceilingOnEven()
            ?.toInt()
            ?: 0

    fun generateJsonSizeObject(
            name: String,
            size: Int,
            children: List<JsonSizeNode>
    ) = JsonSizeObject(
            name = name,
            size = size,
            children = children
    )

    fun generateJsonSizeArray(
            name: String,
            size: Int,
            children: List<JsonSizeNode>
    ) = JsonSizeArray(
            name = name,
            size = size,
            children = children
    )

    fun generateJsonEmpty(
            name: String
    ) = JsonSizeEmpty(name = name)
}
