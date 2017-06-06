package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.jsonParsingFour.*

data class ObjectCloseArrayAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: ObjectClose,
        override val previousClosable: ArrayStructureElement
) : BaseAccumulator<ObjectClose, ArrayStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            ']' -> when (enclosingStructure) {
                is EmptyStructureElement -> closeStructure(::ArrayClose)
                is ArrayStructureElement -> closeStructure(::ArrayClose)
                is ObjectWithKeyStructure -> closeStructure(::ArrayClose)
                is LiteralStructureElement -> TODO("SHOULD NOT HAPPEN")
                is StringStructureElement -> TODO("SHOULD NOT HAPPEN")
                is OpenObjectStructure -> TODO("SHOULD NOT HAPPEN")
            }
            else -> TODO()
        }
    }
}