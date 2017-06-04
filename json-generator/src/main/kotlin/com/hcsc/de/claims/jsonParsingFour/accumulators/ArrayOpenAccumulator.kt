package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.jsonParsingFour.*

data class ArrayOpenAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: ArrayOpen,
        override val previousClosable: ArrayStructureElement
) : BaseAccumulator<ArrayOpen, ArrayStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            ']' -> {

                val newStructureStack = structureStack.dropLast(1)

                val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

                val closeElement = ArrayClose(id = previousClosable.id)

                when (newPreviousStructure) {
                    is EmptyStructureElement -> Success<String, Accumulator<*, *>>(ArrayCloseEmptyAccumulator(
                            idCounter = idCounter,
                            previousElement = closeElement,
                            structure = structure.plus(closeElement)
                    ))
                    is ArrayStructureElement -> Success<String, Accumulator<*, *>>(ArrayCloseArrayAccumulator(
                            idCounter = idCounter,
                            previousElement = closeElement,
                            structure = structure.plus(closeElement),
                            structureStack = newStructureStack,
                            previousClosable = newPreviousStructure
                    ))
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
                    is OpenObjectStructure -> TODO("This should never happen")
                }
            }
            '"' -> openString()
            '[' -> openArray()
            '{' -> openObject()
            else -> openLiteral(char)
        }
    }
}