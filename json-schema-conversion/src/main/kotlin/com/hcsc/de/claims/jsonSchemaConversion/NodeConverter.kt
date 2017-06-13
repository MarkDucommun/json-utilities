package com.hcsc.de.claims.jsonSchemaConversion

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.hcsc.de.claims.kotlinJacksonHelpers.fieldNames


class NodeConverter {

    fun convert(node: JsonNode): SchemaDetail =
            when (node.type) {
                "string" -> when (node.format) {
                    "date" -> Date
                    "date-time" -> DateTime
                    else -> Text(maxLength = node.maxLength.asInt())
                }
                "number" -> Number
                "integer" -> Integer
                "object" -> ComplexObject(properties = node.properties.fieldNames.map {
                    SchemaObject(name = it, detail = convert(node.properties.get(it)))
                })
                "array" -> ArrayDetail(itemType = convert(node.items), maxItems = node.maxItems)
                "other" -> when {
                    node.ref != null -> Reference(type = node.ref.asText())
                    node.oneOf != null -> OneOf(list = (node.oneOf as ArrayNode).map { convert(node = it) })
                    else -> throw Exception("Unhandled schema type")
                }
                else -> throw Exception("Unhandled schema type")
            }
}