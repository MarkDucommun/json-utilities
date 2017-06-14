package io.ducommun.jsonParsing.structureAccumulators

import com.hcsc.de.claims.results.Failure
import io.ducommun.jsonParsing.*
import com.hcsc.de.claims.results.Result

data class ObjectCommaAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: ObjectComma,
        override val previousClosable: OpenObjectStructure
) : BaseAccumulator<ObjectComma, OpenObjectStructure, ObjectChildElement<*>>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> skip
            '"' -> openString()
            else -> fail("object close cannot immediately follow object comma")
        }
    }
}