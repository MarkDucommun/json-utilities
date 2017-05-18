package com.hcsc.de.claims.fileReaders

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

class JacksonJsonFileReader(
        private val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule()
) : JsonFileReader {

    override fun read(filePath: String): JsonNode = objectMapper.readValue(File(filePath))
}
