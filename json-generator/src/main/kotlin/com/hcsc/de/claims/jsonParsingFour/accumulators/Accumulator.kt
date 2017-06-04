package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.jsonParsingFour.JsonStructure
import com.hcsc.de.claims.jsonParsingFour.MainStructure

interface Accumulator<out previousElementType : JsonStructure, out previousClosableType : MainStructure?> {

    val idCounter: Long

    val structure: List<JsonStructure>

    val structureStack: List<MainStructure>

    val previousClosable: previousClosableType

    val previousElement: previousElementType

    fun processChar(char: Char): Result<String, Accumulator<*, *>>
}