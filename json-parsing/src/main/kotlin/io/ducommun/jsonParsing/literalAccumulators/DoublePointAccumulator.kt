package io.ducommun.jsonParsing.literalAccumulators

import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success

data class DoublePointAccumulator(
        val value: String
) : Accumulator {

    override fun addChar(char: Char): Result<String, Accumulator> {

        return when (char) {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> Success(DoubleAccumulator(value = value + char))
            else -> TODO()
        }
    }
}