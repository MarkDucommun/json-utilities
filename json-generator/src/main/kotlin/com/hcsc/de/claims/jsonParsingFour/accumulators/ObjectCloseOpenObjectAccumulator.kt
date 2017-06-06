package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.jsonParsingFour.*

data class ObjectCloseOpenObjectAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: ObjectClose,
        override val previousClosable: OpenObjectStructure
) : BaseAccumulator<ObjectClose, OpenObjectStructure>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            ',' -> addElement(::ObjectComma)
            '}' -> when (enclosingStructure) {
                is EmptyStructureElement -> closeStructure(::ObjectClose)
                is ArrayStructureElement -> closeStructure(::ObjectClose)
                is ObjectWithKeyStructure -> closeStructure(::ObjectClose)
                is LiteralStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is StringStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is OpenObjectStructure -> TODO("THIS SHOULD NEVER HAPPEN")
            }
            else -> TODO()
        }
    }
}