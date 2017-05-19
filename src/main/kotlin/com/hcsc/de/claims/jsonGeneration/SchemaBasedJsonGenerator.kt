package com.hcsc.de.claims.jsonGeneration

import com.hcsc.de.claims.jsonSchemaConversion.SchemaObject

class SchemaBasedJsonGenerator : JsonGenerator<SchemaObject<*>> {

    override fun generate(input: SchemaObject<*>): String {

        return ""
    }
}