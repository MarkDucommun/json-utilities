package com.hcsc.de.claims.jsonParsing

import com.hcsc.de.claims.helpers.Result

interface JsonStructureParser {

    fun parse(input: String): Result<String, List<JsonStructureElement>>
}
