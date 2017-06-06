package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.jsonParsingFour.*

data class ArrayCloseArrayAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: ArrayClose,
        override val previousClosable: ArrayStructureElement
) : BaseAccumulator<ArrayClose, ArrayStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            ',' -> addElement(::ArrayComma)
            ']' -> when (enclosingStructure) {
                is EmptyStructureElement -> closeStructure(::ArrayClose)
                is ArrayStructureElement -> closeStructure(::ArrayClose)
                is ObjectWithKeyStructure -> closeStructure(::ArrayClose)
                is LiteralStructureElement -> fail("How do I get rid of you as a possibility")
                is StringStructureElement -> fail("How do I get rid of you as a possibility")
                is OpenObjectStructure -> fail("How do I get rid of you as a possibility")
            }
            else -> TODO()
        }
    }
}