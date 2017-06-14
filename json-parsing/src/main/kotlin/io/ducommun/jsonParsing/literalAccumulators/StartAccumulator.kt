package io.ducommun.jsonParsing.literalAccumulators

import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success

object StartAccumulator : Accumulator {

    override fun addChar(char: Char): Result<String, Accumulator> {

        return when (char) {
            'n' -> Success(NullAccumulator(previousChar = 'n'))
            't' -> Success(TrueAccumulator(previousChar = 't'))
            'f' -> Success(FalseAccumulator(previousChar = 'f'))
            '1', '2', '3', '4', '5', '6', '7', '8', '9' -> Success(IntegerAccumulator(value = char.toString()))
            '0' -> Success(ZeroAccumulator)
            '-' -> Success(NegativeAccumulator)
            '.' -> Failure("Invalid JSON - float literals must start with a digit")
            else -> Failure("Invalid JSON - '$char' is not a valid literal")
        }
    }
}