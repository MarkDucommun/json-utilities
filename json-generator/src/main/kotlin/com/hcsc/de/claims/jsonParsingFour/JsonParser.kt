package com.hcsc.de.claims.jsonParsingFour

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.flatMap

/**
 * Created by pivotal on 6/13/17.
 */
class JsonParser {

    val jsonStructureParser = JsonStructureParser()
    val jsonStructureNester = JsonStructureNester()
    val jsonStructureTranslator = JsonStructureTranslator()

    fun parse(string: String): Result<String, JsonNode> {

        return jsonStructureParser.parse(string)
                .flatMap { jsonStructureNester.nest(it) }
                .flatMap { jsonStructureTranslator.translate(it) }
    }
}