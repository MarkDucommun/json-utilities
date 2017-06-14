package io.ducommun.jsonParsing.literalAccumulators

import com.hcsc.de.claims.results.Result
import io.ducommun.jsonParsing.JsonNode

interface CompleteAccumulator : Accumulator {

    val node: Result<String, JsonNode>
}