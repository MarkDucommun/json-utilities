package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.jsonParsingFour.*

data class StringOpenAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: StringOpen,
        override val previousClosable: StringStructureElement
) : BaseAccumulator<StringOpen, StringStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            '"' -> {

                val newStructureStack = structureStack.dropLast(1)

                val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

                val closeStringElement = StringClose(id = previousClosable.id)

                when (newPreviousStructure) {
                    is EmptyStructureElement -> Success<String, Accumulator<*, *>>(StringCloseEmptyAccumulator(
                            idCounter = idCounter,
                            previousElement = closeStringElement,
                            structure = structure.plus(closeStringElement)
                    ))
                    is ArrayStructureElement -> Success<String, Accumulator<*, *>>(StringCloseArrayAccumulator(
                            idCounter = idCounter,
                            previousElement = closeStringElement,
                            structure = structure.plus(closeStringElement),
                            previousClosable = newPreviousStructure,
                            structureStack = newStructureStack
                    ))
                    is OpenObjectStructure -> {

                        val modifiedNewPreviousStructure = ObjectWithKeyStructure(id = newPreviousStructure.id)

                        Success<String, Accumulator<*, *>>(ObjectWithKeyAccumulator(
                                idCounter = idCounter,
                                previousElement = closeStringElement,
                                structure = structure.plus(closeStringElement),
                                previousClosable = modifiedNewPreviousStructure,
                                structureStack = newStructureStack.dropLast(1).plus(modifiedNewPreviousStructure)
                        ))
                    }
                    is ObjectWithKeyStructure -> {

                        val modifiedNewPreviousStructure = OpenObjectStructure(id = newPreviousStructure.id)

                        Success<String, Accumulator<*, *>>(StringCloseOpenObjectAccumulator(
                                idCounter = idCounter,
                                previousElement = closeStringElement,
                                structure = structure.plus(closeStringElement),
                                previousClosable = modifiedNewPreviousStructure,
                                structureStack = newStructureStack.dropLast(1).plus(modifiedNewPreviousStructure)
                        ))
                    }
                    is LiteralStructureElement -> fail("How do I get rid of you as a possibility")
                    is StringStructureElement -> fail("How do I get rid of you as a possibility")
                }
            }
            '\\' -> Success<String, Accumulator<*, *>>(StringEscapeAccumulator(
                    idCounter = idCounter,
                    structureStack = structureStack,
                    previousElement = StringEscape,
                    structure = structure,
                    previousClosable = previousClosable
            ))
            else -> {

                val stringElement = StringValue(id = previousClosable.id, value = char)

                Success<String, Accumulator<*, *>>(StringValueAccumulator(
                        idCounter = idCounter,
                        structureStack = structureStack,
                        previousElement = stringElement,
                        structure = structure.plus(stringElement),
                        previousClosable = previousClosable
                ))
            }
        }
    }
}