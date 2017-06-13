package com.hcsc.de.claims.jsonGenerator

import com.hcsc.de.claims.jsonSchemaConversion.SchemaObject
import com.hcsc.de.claims.jsonSchemaConversion.Text
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class SchemaBasedJsonGeneratorTest {

    @Test
    fun `it converts a SchemaObject`() {

        val schemaBasedJsonGenerator = SchemaBasedJsonGenerator()

        val schemaObject = SchemaObject(name = "thing", detail = Text(maxLength = 5))

        val json = schemaBasedJsonGenerator.generate(schemaObject)

        assertThat(json).isEqualTo("{\"thing\":\"XXXXX\"}")
    }
}