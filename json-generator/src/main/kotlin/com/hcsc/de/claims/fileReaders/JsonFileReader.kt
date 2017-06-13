package com.hcsc.de.claims.fileReaders

import com.fasterxml.jackson.databind.JsonNode
import com.hcsc.de.claims.results.Result

interface JsonFileReader {

    fun read(filePath: String): Result<String, JsonNode>
}