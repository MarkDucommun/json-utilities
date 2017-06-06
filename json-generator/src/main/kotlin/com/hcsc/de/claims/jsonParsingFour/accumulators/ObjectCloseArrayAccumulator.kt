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
            ']' -> {

                val newStructureStack = structureStack.dropLast(1)

                val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

                val close = ArrayClose(id = previousClosable.id)

                when (newPreviousStructure) {
                    is EmptyStructureElement -> {

                        Success<String, Accumulator<*, *>>(ArrayCloseEmptyAccumulator(
                                idCounter = idCounter,
                                structure = structure.plus(close),
                                previousElement = close,
                                previousClosable = newPreviousStructure,
                                structureStack = newStructureStack
                        ))
                    }
                    is ArrayStructureElement -> {

                        Success<String, Accumulator<*, *>>(ArrayCloseArrayAccumulator(
                                idCounter = idCounter,
                                structure = structure.plus(close),
                                structureStack = newStructureStack,
                                previousElement = close,
                                previousClosable = newPreviousStructure
                        ))
                    }
                    is ObjectWithKeyStructure -> {

                        val modifiedNewPreviousStructure = OpenObjectStructure(id = newPreviousStructure.id)

                        Success<String, Accumulator<*, *>>(ArrayCloseOpenObjectAccumulator(
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
            else -> TODO()
        }
    }
}