package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.jsonParsingFour.*

data class ObjectReadyForValueAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: ObjectColon,
        override val previousClosable: ObjectWithKeyStructure
) : BaseAccumulator<ObjectColon, ObjectWithKeyStructure, ObjectChildElement<*>>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            '"' -> openString()
            '[' -> openArray()
            '{' -> openObject()
            '}' -> fail("a value must follow a colon")
            else -> openLiteral(char)
        }
    }
}