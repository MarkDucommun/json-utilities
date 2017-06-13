package com.hcsc.de.claims.jsonDeIdentifier

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.hcsc.de.claims.kotlinJacksonHelpers.fieldNames

class JsonDeIdentifier(private val objectMapper: ObjectMapper) {

    fun deidentifyJson(string: JsonString): JsonString = string.deidentify()

    fun JsonString.deidentify(): JsonString {

        val node: JsonNode = objectMapper.readValue(this)

        return node.deidentifyNode().writeValueAsString()
    }

    fun JsonNode.deidentifyNode(): Map<String, Any?> {

        return this.fieldNames.map { name ->

            val value: JsonNode = this[name]

            name to value.deidentifyValue()

        }.toMap()
    }

    fun JsonNode.deidentifyValue(): Any? {
        return when {
            this.isNull -> null
            this.isObject -> deidentifyNode()
            this.isArray -> map { it.deidentifyValue() }
            this.isNumber -> "".padEnd(asText().length, '#')
            else -> "".padEnd(asText().length, '?')
        }
    }

    fun Any.writeValueAsString(): String = objectMapper.writeValueAsString(this)
}

typealias JsonString = String