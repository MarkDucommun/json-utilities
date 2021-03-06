package io.ducommun.jsonParsing.literalAccumulators

import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success
import io.ducommun.jsonParsing.IntegerNode
import io.ducommun.jsonParsing.JsonNode

object ZeroAccumulator : CompleteAccumulator {

    override val node: Result<String, JsonNode> = Success(IntegerNode(value = 0))

    override fun addChar(char: Char): Result<String, Accumulator> {

        return when (char) {
            '.' -> Success(DoublePointAccumulator(value = "0."))
            'E', 'e' -> Success(DoubleExponentAccumulator(value = "0E"))
            else -> Failure("Invalid JSON - leading zeros are not permitted")
        }
    }
}