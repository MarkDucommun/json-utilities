package com.hcsc.de.claims.fileReaders

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import java.io.File
import java.io.FileNotFoundException

class JacksonJsonFileReader(
        private val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule()
) : JsonFileReader {

    override fun read(filePath: String): Result<String, JsonNode> {
        return try {
            Success(objectMapper.readValue(File(filePath)))
        } catch (e: FileNotFoundException) {
            Failure("File at '$filePath' was not found")
        } catch (e: JsonParseException) {
            Failure("File '$filePath' did not contain a valid JSON object")
        } catch (e: Exception) {
            Failure("Unknown error while reading File at $filePath")
        }
    }
}
