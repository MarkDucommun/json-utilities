package io.ducommun.jsonParsing.structureAccumulators

import io.ducommun.jsonParsing.ArrayComma
import io.ducommun.jsonParsing.ArrayStructureElement
import io.ducommun.jsonParsing.JsonStructure
import io.ducommun.jsonParsing.MainStructure
import com.hcsc.de.claims.results.Result

data class ArrayCommaAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: ArrayComma,
        override val previousClosable: ArrayStructureElement
) : BaseAccumulator<ArrayComma, ArrayStructureElement, MainStructure<*>>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            ' ', '\n', '\r', '\t' -> skip
            '"' -> openString()
            '[' -> openArray()
            '{' -> openObject()
            else -> openLiteral(char)
        }
    }
}