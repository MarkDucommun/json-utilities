package com.hcsc.de.claims.jsonParsingFour.structureAccumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.jsonParsingFour.*

data class ArrayOpenAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: ArrayOpen,
        override val previousClosable: ArrayStructureElement
) : BaseAccumulator<ArrayOpen, ArrayStructureElement, MainStructure<*>>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> skip
            ']' -> when (enclosingStructure) {
                is EmptyStructureElement -> closeStructure(::ArrayClose)
                is ArrayStructureElement -> closeStructure(::ArrayClose)
                is ObjectWithKeyStructure -> closeStructure(::ArrayClose)
                is LiteralStructureElement -> TODO("This should never happen")
                is StringStructureElement -> TODO("This should never happen")
                is OpenObjectStructure -> TODO("This should never happen")
            }
            '"' -> openString()
            '[' -> openArray()
            '{' -> openObject()
            else -> openLiteral(char)
        }
    }
}