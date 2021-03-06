package io.ducommun.jsonParsing.literalAccumulators

import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success
import com.hcsc.de.claims.results.wrapExternalLibraryUsageAsResult
import io.ducommun.jsonParsing.IntegerNode
import io.ducommun.jsonParsing.JsonNode

data class IntegerAccumulator(
        val value: String
) : CompleteAccumulator {

    override fun addChar(char: Char): Result<String, Accumulator> {

        return when (char) {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> Success(copy(value = value + char))
            '.' -> Success(DoublePointAccumulator(value = value + char))
            'E', 'e' -> Success(DoubleExponentAccumulator(value = value + char))
            else -> Failure("Invalid JSON - '$char' may not be part of a number")
        }
    }

    override val node: Result<String, JsonNode>
        get() = wrapExternalLibraryUsageAsResult { IntegerNode(value = value.toLong()) }
}