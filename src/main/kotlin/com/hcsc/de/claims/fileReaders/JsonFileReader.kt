package com.hcsc.de.claims.fileReaders

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.hcsc.de.claims.helpers.Result
import java.io.File

interface JsonFileReader {

    fun read(filePath: String): Result<String, JsonNode>
}