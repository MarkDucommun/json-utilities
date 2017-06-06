package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.jsonParsingFour.*

data class LiteralValueAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: LiteralValue,
        override val previousClosable: LiteralStructureElement
) : BaseAccumulator<LiteralValue, LiteralStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        val newStructureStack = structureStack.dropLast(1)

        val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

        return when (char) {
            ' ', '\n', '\r', '\t' -> when (enclosingStructure) {
                is EmptyStructureElement -> closeLiteral()
                is ArrayStructureElement -> closeLiteral()
                is ObjectWithKeyStructure -> closeLiteral()
                else -> TODO("THIS REALLY SHOULD NEVER HAPPEN")
            }
            ',' -> when (newPreviousStructure) {
                is ArrayStructureElement -> {

                    val arrayComma = ArrayComma(newPreviousStructure.id)

                    Success<String, Accumulator<*, *>>(ArrayCommaAccumulator(
                            idCounter = idCounter,
                            structure = structure.dropLast(1)
                                    .plus(LiteralClose(id = previousElement.id, value = previousElement.value))
                                    .plus(arrayComma),
                            previousElement = arrayComma,
                            structureStack = newStructureStack,
                            previousClosable = newPreviousStructure
                    ))
                }
                is ObjectWithKeyStructure -> {

                    val objectComma = ObjectComma(id = newPreviousStructure.id)

                    val modifiedNewPreviousStructure = OpenObjectStructure(id = newPreviousStructure.id)

                    Success<String, Accumulator<*, *>>(ObjectCommaAccumulator(
                            idCounter = idCounter,
                            structure = structure.dropLast(1)
                                    .plus(LiteralClose(id = previousElement.id, value = previousElement.value))
                                    .plus(objectComma),
                            previousElement = objectComma,
                            structureStack = newStructureStack.dropLast(1).plus(modifiedNewPreviousStructure),
                            previousClosable = modifiedNewPreviousStructure
                    ))
                }
                is OpenObjectStructure -> TODO("THIS SHOULD NEVER HAPPEN")
                EmptyStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is LiteralStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is StringStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
            }
            ']' -> when (newPreviousStructure) {
                is ArrayStructureElement -> {

                    val evenNewerStructureStack = newStructureStack.dropLast(1)

                    val evenNewerPreviousStructure = evenNewerStructureStack.lastOrNull() ?: EmptyStructureElement

                    val arrayClose = ArrayClose(newPreviousStructure.id)

                    when (evenNewerPreviousStructure) {
                        EmptyStructureElement -> Success<String, Accumulator<*, *>>(ArrayCloseEmptyAccumulator(
                                idCounter = idCounter,
                                structure = structure.dropLast(1)
                                        .plus(LiteralClose(id = previousElement.id, value = previousElement.value))
                                        .plus(arrayClose),
                                previousElement = arrayClose,
                                previousClosable = EmptyStructureElement,
                                structureStack = evenNewerStructureStack
                        ))
                        is ArrayStructureElement -> Success<String, Accumulator<*, *>>(ArrayCloseArrayAccumulator(
                                idCounter = idCounter,
                                previousElement = arrayClose,
                                structure = structure.dropLast(1)
                                        .plus(LiteralClose(id = previousElement.id, value = previousElement.value))
                                        .plus(arrayClose),
                                structureStack = evenNewerStructureStack,
                                previousClosable = evenNewerPreviousStructure
                        ))
                        is ObjectWithKeyStructure -> {

                            val modifiedEvenNewerPreviousStructure = OpenObjectStructure(id = evenNewerPreviousStructure.id)

                            Success<String, Accumulator<*, *>>(ArrayCloseOpenObjectAccumulator(
                                    idCounter = idCounter,
                                    previousElement = arrayClose,
                                    structure = structure.dropLast(1)
                                            .plus(LiteralClose(id = previousElement.id, value = previousElement.value))
                                            .plus(arrayClose),
                                    structureStack = evenNewerStructureStack.dropLast(1).plus(modifiedEvenNewerPreviousStructure),
                                    previousClosable = modifiedEvenNewerPreviousStructure
                            ))
                        }
                        is LiteralStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                        is StringStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                        is OpenObjectStructure -> TODO("THIS SHOULD NEVER HAPPEN")
                    }
                }
                EmptyStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is LiteralStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is StringStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is OpenObjectStructure -> TODO("THIS SHOULD NEVER HAPPEN")
                is ObjectWithKeyStructure -> TODO("THIS SHOULD NEVER HAPPEN")
            }
            '}' -> when (newPreviousStructure) {
                is ObjectWithKeyStructure -> {

                    val evenNewerStructureStack = newStructureStack.dropLast(1)

                    val evenNewerPreviousStructure = evenNewerStructureStack.lastOrNull() ?: EmptyStructureElement

                    val objectClose = ObjectClose(newPreviousStructure.id)

                    when (evenNewerPreviousStructure) {
                        is EmptyStructureElement -> Success<String, Accumulator<*, *>>(ObjectCloseEmptyAccumulator(
                                idCounter = idCounter,
                                structure = structure.dropLast(1)
                                        .plus(LiteralClose(id = previousElement.id, value = previousElement.value))
                                        .plus(objectClose),
                                previousElement = objectClose,
                                structureStack = evenNewerStructureStack,
                                previousClosable = evenNewerPreviousStructure
                        ))
                        is ArrayStructureElement -> Success<String, Accumulator<*, *>>(ObjectCloseArrayAccumulator(
                                idCounter = idCounter,
                                structure = structure.dropLast(1)
                                        .plus(LiteralClose(id = previousElement.id, value = previousElement.value))
                                        .plus(objectClose),
                                previousElement = objectClose,
                                structureStack = evenNewerStructureStack,
                                previousClosable = evenNewerPreviousStructure
                        ))
                        is ObjectWithKeyStructure -> {

                            val modifiedEvenNewerPreviousStructure = OpenObjectStructure(id = evenNewerPreviousStructure.id)

                            Success<String, Accumulator<*, *>>(ObjectCloseOpenObjectAccumulator(
                                    idCounter = idCounter,
                                    structure = structure.dropLast(1)
                                            .plus(LiteralClose(id = previousElement.id, value = previousElement.value))
                                            .plus(objectClose),
                                    previousElement = objectClose,
                                    structureStack = evenNewerStructureStack.dropLast(1).plus(modifiedEvenNewerPreviousStructure),
                                    previousClosable = modifiedEvenNewerPreviousStructure
                            ))
                        }
                        is StringStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                        is LiteralStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                        is OpenObjectStructure -> TODO("THIS SHOULD NEVER HAPPEN")
                    }
                }
                EmptyStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is LiteralStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is StringStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is ArrayStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is OpenObjectStructure -> TODO("THIS SHOULD NEVER HAPPEN")
            }
            else -> addValue(::LiteralValue, char)
        }
    }

    fun closeLiteral(): Result<String, Accumulator<*, *>> =
            replaceLastElementAndCloseStructure(LiteralClose(
                    id = previousElement.id,
                    value = previousElement.value
            ))
}