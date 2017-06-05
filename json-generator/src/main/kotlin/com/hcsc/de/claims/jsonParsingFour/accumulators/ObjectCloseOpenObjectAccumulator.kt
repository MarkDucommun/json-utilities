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
                            previousClosable = newPreviousStructure,
                            structureStack = newStructureStack
                    ))
                    is ArrayStructureElement -> Success<String, Accumulator<*, *>>(ObjectCloseArrayAccumulator(
                            idCounter = idCounter,
                            structure = structure.plus(close),
                            structureStack = newStructureStack,
                            previousElement = close,
                            previousClosable = newPreviousStructure
                    ))
                    is ObjectWithKeyStructure -> {

                        val modifiedNewPreviousStructure = OpenObjectStructure(id = newPreviousStructure.id)

                        Success<String, Accumulator<*, *>>(ObjectCloseOpenObjectAccumulator(
                                idCounter = idCounter,
                                structureStack = newStructureStack.dropLast(1).plus(modifiedNewPreviousStructure),
                                structure = structure.plus(close),
                                previousClosable = modifiedNewPreviousStructure,
                                previousElement = close
                        ))
                    }
                    is LiteralStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                    is StringStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                    is OpenObjectStructure -> TODO("THIS SHOULD NEVER HAPPEN")
                }
            }
            else -> TODO()
        }
    }
}