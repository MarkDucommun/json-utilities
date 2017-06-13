package com.hcsc.de.claims.fileReading

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class JacksonJsonFileReader(
        private val objectMapper: com.fasterxml.jackson.databind.ObjectMapper = com.fasterxml.jackson.databind.ObjectMapper().registerKotlinModule()
) : JsonFileReader {

    override fun read(filePath: String): com.hcsc.de.claims.results.Result<String, JsonNode> {
        return try {
            com.hcsc.de.claims.results.Success(objectMapper.readValue(java.io.File(filePath)))
        } catch (e: java.io.FileNotFoundException) {
            com.hcsc.de.claims.results.Failure("File at '$filePath' was not found")
        } catch (e: com.fasterxml.jackson.core.JsonParseException) {
            com.hcsc.de.claims.results.Failure("File '$filePath' did not contain a valid JSON object")
        } catch (e: Exception) {
            com.hcsc.de.claims.results.Failure("Unknown error while reading File at $filePath")
        }
    }
}
