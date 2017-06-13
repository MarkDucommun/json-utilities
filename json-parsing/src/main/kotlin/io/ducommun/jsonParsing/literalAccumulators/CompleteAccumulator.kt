package io.ducommun.jsonParsing.literalAccumulators

import io.ducommun.jsonParsing.JsonNode

interface CompleteAccumulator : Accumulator {

    val node: JsonNode
}