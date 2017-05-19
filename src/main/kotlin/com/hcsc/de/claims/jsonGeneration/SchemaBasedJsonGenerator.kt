package com.hcsc.de.claims.jsonGeneration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.hcsc.de.claims.jsonSchemaConversion.SchemaObject

class SchemaBasedJsonGenerator(
        private val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule()
) : JsonGenerator<SchemaObject<*>> {

    override fun generate(input: SchemaObject<*>): String {

        return mapOf(input.name to input.detail.toJsonable().ejected).writeAsString()
    }

    private fun Any.writeAsString(): String = objectMapper.writeValueAsString(this)
}