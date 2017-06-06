package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.jsonParsingFour.*

data class ArrayCloseOpenObjectAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: ArrayClose,
        override val previousClosable: OpenObjectStructure
) : BaseAccumulator<ArrayClose, OpenObjectStructure>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            ',' -> {

                val objectComma = ObjectComma(id = previousClosable.id)

                Success(ObjectCommaAccumulator(
                        idCounter = idCounter,
                        structureStack = structureStack,
                        structure = structure.plus(objectComma),
                        previousClosable = previousClosable,
                        previousElement = objectComma
                ))
            }
            '}' -> {

                val newStructureStack = structureStack.dropLast(1)

                val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

                val close = ObjectClose(id = previousClosable.id)

                when (newPreviousStructure) {
                    is EmptyStructureElement -> Success<String, Accumulator<*, *>>(ObjectCloseEmptyAccumulator(
                            idCounter = idCounter,
                            structure = structure.plus(close),
                            previousElement = close,
                            structureStack = newStructureStack,
                            previousClosable = newPreviousStructure
                    ))
                    is ArrayStructureElement -> Success<String, Accumulator<*, *>>(ObjectCloseArrayAccumulator(
                            idCounter = idCounter,
                            structureStack = newStructureStack,
                            previousClosable = newPreviousStructure,
                            previousElement = close,
                            structure = structure.plus(close)
                    ))
                    is ObjectWithKeyStructure -> {

                        val modifiedNewPreviousStructure = OpenObjectStructure(id = newPreviousStructure.id)

                        Success<String, Accumulator<*, *>>(ObjectCloseOpenObjectAccumulator(
                                idCounter = idCounter,
                                structure = structure.plus(close),
                                previousElement = close,
                                structureStack = newStructureStack.dropLast(1).plus(modifiedNewPreviousStructure),
                                previousClosable = modifiedNewPreviousStructure
                        ))
                    }
                    is LiteralStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                    is StringStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                    is OpenObjectStructure -> TODO("THIS SHOULD NEVER HAPPEN")
                }
            }
            else -> fail("must close an object with a curly brace")
        }
    }
}