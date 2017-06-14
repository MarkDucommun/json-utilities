package io.ducommun.jsonParsing.literalAccumulators

import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success
import io.ducommun.jsonParsing.FalseNode
import io.ducommun.jsonParsing.JsonNode
import io.ducommun.jsonParsing.NullNode
import io.ducommun.jsonParsing.TrueNode

data class LiteralCompleteAccumulator(
        val rawNode: JsonNode
) : CompleteAccumulator {

    override val node: Result<String, JsonNode> = Success(rawNode)

    override fun addChar(char: Char): Result<String, Accumulator> {
        return when (rawNode) {
            is NullNode -> "null"
            is TrueNode -> "true"
            is FalseNode -> "false"
            else -> "?"
        }.plus(char).let { invalidStructure ->
            Failure("Invalid JSON - $invalidStructure")
        }
    }
}