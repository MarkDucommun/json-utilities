package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.jsonParsingFour.*

data class ObjectOpenAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: ObjectOpen,
        override val previousClosable: OpenObjectStructure
) : BaseAccumulator<ObjectOpen, OpenObjectStructure>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            '"' -> openString()
            '}' -> {

                val newStructureStack = structureStack.dropLast(1)

                val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

                val close = ObjectClose(id = previousClosable.id)

                when (newPreviousStructure) {
                    is EmptyStructureElement -> {

                        Success<String, Accumulator<*, *>>(ObjectCloseEmptyAccumulator(
                                idCounter = idCounter,
                                structure = structure.plus(close),
                                previousElement = close
                        ))
                    }
                    is ArrayStructureElement -> {

                        Success<String, Accumulator<*, *>>(ObjectCloseArrayAccumulator(
                                idCounter = idCounter,
                                structure = structure.plus(close),
                                structureStack = newStructureStack,
                                previousElement = close,
                                previousClosable = newPreviousStructure
                        ))
                    }
                    is ObjectWithKeyStructure -> {

                        val modifiedNewPreviousStructure = OpenObjectStructure(id = newPreviousStructure.id)

                        Success<String, Accumulator<*, *>>(ObjectCloseOpenObjectAccumulator(
                                idCounter = idCounter,
                                structure = structure.plus(close),
                                structureStack = newStructureStack.dropLast(1).plus(modifiedNewPreviousStructure),
                                previousElement = close,
                                previousClosable = modifiedNewPreviousStructure
                        ))
                    }
                    is LiteralStructureElement -> TODO("SHOULD NOT HAPPEN")
                    is StringStructureElement -> TODO("SHOULD NOT HAPPEN")
                    is OpenObjectStructure -> TODO("SHOULD NOT HAPPEN")
                }
            }
            else -> fail("object key must be a string")
        }
    }
}