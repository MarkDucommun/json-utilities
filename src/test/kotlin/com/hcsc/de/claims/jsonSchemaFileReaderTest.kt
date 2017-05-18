package com.hcsc.de.claims

import com.fasterxml.jackson.databind.JsonNode
import com.hcsc.de.claims.fileReaders.JacksonJsonFileReader
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class jsonSchemaFileReaderTest {

    val fileReader = JacksonJsonFileReader()

    @Test
    fun `file reader reads from a file into a JSON node`() {

        val node = fileReader.read("/Users/xpdesktop/workspace/demo/fake-claims-generator/src/main/resources/cts-schema.json")

        assertThat(node is JsonNode).isTrue()
    }
}