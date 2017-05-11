package com.hcsc.de.claims

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

class JacksonFileReader : FileReader {

    override fun read(filePath: String): JsonNode = ObjectMapper().registerKotlinModule().readValue(File(filePath), JsonNode::class.java)
}
