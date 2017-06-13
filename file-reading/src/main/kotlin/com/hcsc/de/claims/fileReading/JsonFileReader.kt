package com.hcsc.de.claims.fileReading

import com.fasterxml.jackson.databind.JsonNode

interface JsonFileReader {

    fun read(filePath: String): com.hcsc.de.claims.results.Result<String, JsonNode>
}