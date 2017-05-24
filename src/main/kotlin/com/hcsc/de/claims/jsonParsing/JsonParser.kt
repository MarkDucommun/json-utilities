package com.hcsc.de.claims.jsonParsing

import com.hcsc.de.claims.helpers.Result

interface JsonParser {

    fun parse(input: String): Result<String, JsonNode>
}