package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.results.Result

interface JsonSizeAnalyzer {

    fun generateJsonSizeOverview(nodes: List<JsonSizeNode>): Result<String, JsonSizeOverview<Int>>
}