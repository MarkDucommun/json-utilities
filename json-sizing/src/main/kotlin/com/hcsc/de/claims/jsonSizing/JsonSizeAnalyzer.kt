package com.hcsc.de.claims.jsonSizing

interface JsonSizeAnalyzer {

    fun generateJsonSizeOverview(nodes: List<JsonSizeNode>): com.hcsc.de.claims.results.Result<String, JsonSizeOverview<Int>>
}