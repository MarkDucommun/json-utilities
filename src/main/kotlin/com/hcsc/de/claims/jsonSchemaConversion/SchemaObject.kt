package com.hcsc.de.claims.jsonSchemaConversion

import com.fasterxml.jackson.databind.ObjectMapper

data class SchemaObject<out detailType : SchemaDetail>(
        val name: String,
        val detail: detailType
) {
    fun toJson(): String {

        return ObjectMapper().writeValueAsString(mapOf(name to detail.toJsonable()))
    }
}