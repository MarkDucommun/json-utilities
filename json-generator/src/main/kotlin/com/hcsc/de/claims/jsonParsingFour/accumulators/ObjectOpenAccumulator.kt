package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.jsonParsingFour.*

data class ObjectOpenAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: ObjectOpen,
        override val previousClosable: OpenObjectStructure
) : BaseAccumulator<ObjectOpen, OpenObjectStructure, MainStructure<*>>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            '"' -> openString()
            '}' -> when (enclosingStructure) {
                is EmptyStructureElement -> closeStructure(::ObjectClose)
                is ArrayStructureElement -> closeStructure(::ObjectClose)
                is ObjectWithKeyStructure -> closeStructure(::ObjectClose)
                is LiteralStructureElement -> TODO("SHOULD NOT HAPPEN")
                is StringStructureElement -> TODO("SHOULD NOT HAPPEN")
                is OpenObjectStructure -> TODO("SHOULD NOT HAPPEN")
            }
            else -> fail("object key must be a string")
        }
    }
}