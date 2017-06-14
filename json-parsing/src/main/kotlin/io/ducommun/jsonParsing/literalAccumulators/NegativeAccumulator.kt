package io.ducommun.jsonParsing.literalAccumulators

import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success

object NegativeAccumulator : Accumulator {

    override fun addChar(char: Char): Result<String, Accumulator> {

        return when (char) {
            '1', '2', '3', '4', '5', '6', '7', '8', '9' -> Success(IntegerAccumulator(value = "-$char"))
            '0' -> Success(NegativeZeroAccumulator)
            else -> Failure("Invalid JSON - '$char' may not be part of a number")
        }
    }
}