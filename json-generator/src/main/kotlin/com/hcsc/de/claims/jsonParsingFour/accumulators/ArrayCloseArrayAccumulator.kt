package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.jsonParsingFour.*

data class ArrayCloseArrayAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val previousElement: ArrayClose,
        override val structureStack: List<MainStructure>,
        override val previousClosable: ArrayStructureElement
) : BaseAccumulator<ArrayClose, ArrayStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            ',' -> {

                val arrayComma = ArrayComma(previousClosable.id)

                Success<String, Accumulator<*, *>>(ArrayCommaAccumulator(
                        idCounter = idCounter,
                        structure = structure.plus(arrayComma),
                        previousElement = arrayComma,
                        structureStack = structureStack,
                        previousClosable = previousClosable
                ))
            }
            ']' -> {

                val newStructureStack = structureStack.dropLast(1)

                val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

                val closeElement = ArrayClose(id = previousClosable.id)

                when (newPreviousStructure) {
                    is EmptyStructureElement -> {

                        Success<String, Accumulator<*, *>>(ArrayCloseEmptyAccumulator(
                                idCounter = idCounter,
                                previousElement = closeElement,
                                structure = structure.plus(closeElement)
                        ))
                    }
                    is ArrayStructureElement -> {

                        Success<String, Accumulator<*, *>>(ArrayCloseArrayAccumulator(
                                idCounter = idCounter,
                                previousElement = closeElement,
                                structure = structure.plus(closeElement),
                                structureStack = newStructureStack,
                                previousClosable = newPreviousStructure
                        ))
                    }
                    is ObjectWithKeyStructure -> {

                        val modifiedNewPreviousStructure = OpenObjectStructure(id = newPreviousStructure.id)

                        Success<String, Accumulator<*, *>>(ArrayCloseOpenObjectAccumulator(
                                idCounter = idCounter,
                                previousElement = closeElement,
                                structure = structure.plus(closeElement),
                                structureStack = newStructureStack.dropLast(1).plus(modifiedNewPreviousStructure),
                                previousClosable = modifiedNewPreviousStructure
                        ))
                    }
                    is LiteralStructureElement -> fail("How do I get rid of you as a possibility")
                    is StringStructureElement -> fail("How do I get rid of you as a possibility")
                    is OpenObjectStructure -> fail("How do I get rid of you as a possibility")
                }
            }
            else -> TODO()
        }
    }
}