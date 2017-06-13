package io.ducommun.jsonParsing.literalAccumulators

import com.hcsc.de.claims.results.Result

interface Accumulator {

    fun addChar(char: Char): Result<String, Accumulator>
}