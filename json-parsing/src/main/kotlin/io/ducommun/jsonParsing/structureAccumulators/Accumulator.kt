package io.ducommun.jsonParsing.structureAccumulators

import com.hcsc.de.claims.results.Result
import io.ducommun.jsonParsing.JsonStructure
import io.ducommun.jsonParsing.MainStructure

interface Accumulator<out previousElementType : JsonStructure, out previousClosableType : MainStructure<*>> {

    val idCounter: Long

    val structure: List<JsonStructure>

    val structureStack: List<MainStructure<*>>

    val previousClosable: previousClosableType

    val previousElement: previousElementType

    fun processChar(char: Char): Result<String, Accumulator<*, *>>
}