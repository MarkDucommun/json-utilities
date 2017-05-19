package com.hcsc.de.claims.jsonSchemaConversion

data class SchemaObject<out detailType : SchemaDetail>(
        val name: String,
        val detail: detailType
)