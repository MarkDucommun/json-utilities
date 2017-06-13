package io.ducommun.jsonParsing

import com.hcsc.de.claims.results.flatMap

class JsonParser {

    val jsonStructureParser = JsonStructureParser()
    val jsonStructureNester = JsonStructureNester()
    val jsonStructureTranslator = JsonStructureTranslator()

    fun parse(string: String): com.hcsc.de.claims.results.Result<String, JsonNode> {

        return jsonStructureParser.parse(string)
                .flatMap { jsonStructureNester.nest(it) }
                .flatMap { jsonStructureTranslator.translate(it) }
    }
}