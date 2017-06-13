package com.hcsc.de.claims.jsonSchemaConversion

import com.fasterxml.jackson.databind.JsonNode
import com.hcsc.de.claims.kotlinJacksonHelpers.fieldNames

class ListConverter(private val nodeConverter: NodeConverter) {

    fun convert(node: JsonNode): List<SchemaObject<*>> {
        return node.fieldNames.map { SchemaObject(name = it, detail = nodeConverter.convert(node.get(it))) }
    }
}