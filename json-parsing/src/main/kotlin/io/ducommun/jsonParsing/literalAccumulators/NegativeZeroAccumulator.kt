package io.ducommun.jsonParsing.literalAccumulators

import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success

object NegativeZeroAccumulator : Accumulator {

    override fun addChar(char: Char): Result<String, Accumulator> {

        return when (char) {
            '.' -> Success(DoublePointAccumulator(value = "-0."))
            else -> Failure("Invalid JSON - leading zeros are not permitted")
        }
    }
}