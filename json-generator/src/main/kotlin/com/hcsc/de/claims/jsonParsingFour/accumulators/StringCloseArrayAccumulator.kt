package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.jsonParsingFour.*

data class StringCloseArrayAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: StringClose,
        override val previousClosable: ArrayStructureElement
) : BaseAccumulator<StringClose, ArrayStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            ']' -> {

                val newStructureStack = structureStack.dropLast(1)

                val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

                val closeElement = ArrayClose(id = previousClosable.id)

                when (newPreviousStructure) {
                    is EmptyStructureElement -> {

                        Success<String, Accumulator<*, *>>(ArrayCloseEmptyAccumulator(
                                idCounter = idCounter,
                                previousElement = closeElement,
                                structure = structure.plus(closeElement),
                                previousClosable = newPreviousStructure,
                                structureStack = newStructureStack
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
            else -> TODO()
        }
    }
}