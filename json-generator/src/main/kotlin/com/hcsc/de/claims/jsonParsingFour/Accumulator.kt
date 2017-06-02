package com.hcsc.de.claims.jsonParsingFour

import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success

sealed class Accumulator<out previousElementType : JsonStructure, out previousClosableType : MainStructure?> {

    abstract val idCounter: Long

    abstract val structure: List<JsonStructure>

    abstract val structureStack: List<MainStructure>

    abstract val previousClosable: previousClosableType

    abstract val previousElement: previousElementType

    abstract fun processChar(char: Char): Result<String, Accumulator<*, *>>

//    fun addNewStructure(constructor: () -> MainStructure): Accumulator<previousElementType, previousClosableType> {
//
//    }

    fun fail(message: String): Failure<String, Accumulator<*, *>> = Failure("Invalid JSON - $message")

    val unmodified: Success<String, Accumulator<*, *>> get() = Success(this)
}

sealed class EmptyAccumulator<out previousElementType : JsonStructure> : Accumulator<previousElementType, EmptyStructureElement>() {

    override val previousClosable: EmptyStructureElement = EmptyStructureElement

    override val structureStack: List<MainStructure> = listOf(EmptyStructureElement)
}

object RootAccumulator : Accumulator<EmptyStructureElement, EmptyStructureElement>() {

    override val idCounter: Long = 1

    override val structure: List<JsonStructure> = emptyList()

    override val previousElement: EmptyStructureElement = EmptyStructureElement

    override val structureStack: List<MainStructure> = listOf(EmptyStructureElement)

    override val previousClosable: EmptyStructureElement = EmptyStructureElement

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            '"' -> {

                val stringStructureElement = StringStructureElement(id = idCounter)

                val openStringElement = StringOpen(id = idCounter)

                Success(StringOpenAccumulator(
                        idCounter = idCounter + 1,
                        structureStack = structureStack.plus(stringStructureElement),
                        previousElement = openStringElement,
                        previousClosable = stringStructureElement,
                        structure = listOf(openStringElement)
                ))
            }
            '[' -> {

                val structureElement = ArrayStructureElement(id = idCounter)

                val openElement = ArrayOpen(id = idCounter)

                Success(ArrayOpenAccumulator(
                        idCounter = idCounter + 1,
                        structureStack = structureStack.plus(structureElement),
                        previousElement = openElement,
                        previousClosable = structureElement,
                        structure = listOf(openElement)
                ))
            }
            else -> {

                val literalElement = LiteralStructureElement(id = idCounter)

                val literalChild = LiteralValue(value = char, id = idCounter)

                Success(LiteralValueAccumulator(
                        idCounter = idCounter + 1,
                        structureStack = structureStack.plus(literalElement),
                        previousElement = literalChild,
                        previousClosable = literalElement,
                        structure = structure.plus(literalChild)
                ))
            }
        }
    }
}

data class LiteralValueAccumulator(
        override val idCounter: Long,
        override val structureStack: List<MainStructure>,
        override val previousClosable: LiteralStructureElement,
        override val previousElement: LiteralValue,
        override val structure: List<JsonStructure>
) : Accumulator<LiteralValue, LiteralStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        val newStructureStack = structureStack.dropLast(1)

        val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

        return when (char) {
            ' ', '\n', '\r', '\t' -> when (newPreviousStructure) {
                EmptyStructureElement -> {

                    val literalChildCloseElement = LiteralClose(
                            id = previousElement.id,
                            value = previousElement.value
                    )

                    Success<String, Accumulator<*, *>>(LiteralCloseEmptyAccumulator(
                            idCounter = previousClosable.id,
                            previousElement = literalChildCloseElement,
                            structure = structure.dropLast(1).plus(literalChildCloseElement)
                    ))
                }
                is LiteralStructureElement -> TODO("THIS REALLY SHOULD NEVER HAPPEN")
                is StringStructureElement -> TODO()
                is ArrayStructureElement -> {

                    val literalChildCloseElement = LiteralClose(
                            id = previousElement.id,
                            value = previousElement.value
                    )

                    Success<String, Accumulator<*, *>>(LiteralCloseArrayAccumulator(
                            idCounter = idCounter,
                            previousElement = literalChildCloseElement,
                            previousClosable = newPreviousStructure,
                            structure = structure.dropLast(1).plus(literalChildCloseElement),
                            structureStack = newStructureStack
                    ))
                }
                is ObjectStructureElement -> TODO()
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
                else -> TODO()
            }
            ']' -> when (newPreviousStructure) {
                is ArrayStructureElement -> {

                    val evenNewerStructureStack = newStructureStack.dropLast(1)

                    val evenNewerPreviousStructure = evenNewerStructureStack.lastOrNull() ?: EmptyStructureElement

                    when (evenNewerPreviousStructure) {
                        EmptyStructureElement -> {

                            val arrayClose = ArrayClose(newPreviousStructure.id)

                            Success<String, Accumulator<*, *>>(ArrayCloseEmptyAccumulator(
                                    idCounter = idCounter,
                                    structure = structure.dropLast(1)
                                            .plus(LiteralClose(id = previousElement.id, value = previousElement.value))
                                            .plus(arrayClose),
                                    previousElement = arrayClose
                            ))
                        }
                        is LiteralStructureElement -> TODO()
                        is StringStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                        is ArrayStructureElement -> {

                            val closeElement = ArrayClose(id = newPreviousStructure.id)

                            Success<String, Accumulator<*, *>>(ArrayCloseArrayAccumulator(
                                    idCounter = idCounter,
                                    previousElement = closeElement,
                                    structure = structure.dropLast(1)
                                            .plus(LiteralClose(id = previousElement.id, value = previousElement.value))
                                            .plus(closeElement),
                                    structureStack = evenNewerStructureStack,
                                    previousClosable = evenNewerPreviousStructure
                            ))
                        }
                        is ObjectStructureElement -> TODO()
                    }
                }
                else -> TODO()
            }
            else -> {

                val literalChild = LiteralValue(value = char, id = previousClosable.id)

                Success(LiteralValueAccumulator(
                        idCounter = idCounter,
                        structureStack = structureStack,
                        previousElement = literalChild,
                        previousClosable = previousClosable,
                        structure = structure.plus(literalChild)
                ))
            }
        }
    }
}

data class LiteralCloseEmptyAccumulator(
        override val idCounter: Long,
        override val previousElement: LiteralClose,
        override val structure: List<JsonStructure>
) : EmptyAccumulator<LiteralClose>() {

    override val structureStack: List<MainStructure> = listOf(EmptyStructureElement)

    override val previousClosable: EmptyStructureElement = EmptyStructureElement

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            else -> fail("nothing can follow a closed root literal")
        }
    }
}

data class StringOpenAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: StringOpen,
        override val previousClosable: StringStructureElement
) : Accumulator<StringOpen, StringStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            '"' -> {

                val newStructureStack = structureStack.dropLast(1)

                val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

                when (newPreviousStructure) {
                    is EmptyStructureElement -> {

                        val closeStringElement = StringClose(id = previousClosable.id)

                        Success<String, Accumulator<*, *>>(StringCloseEmptyAccumulator(
                                idCounter = idCounter,
                                previousElement = closeStringElement,
                                structure = structure.plus(closeStringElement)
                        ))
                    }
                    is LiteralStructureElement -> fail("How do I get rid of you as a possibility")
                    is StringStructureElement -> fail("How do I get rid of you as a possibility")
                    is ArrayStructureElement -> {

                        val closeStringElement = StringClose(id = previousClosable.id)

                        Success<String, Accumulator<*, *>>(StringCloseArrayAccumulator(
                                idCounter = idCounter,
                                previousElement = closeStringElement,
                                structure = structure.plus(closeStringElement),
                                previousClosable = newPreviousStructure,
                                structureStack = newStructureStack
                        ))
                    }
                    is ObjectStructureElement -> TODO()
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

data class StringValueAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: StringValue,
        override val previousClosable: StringStructureElement
) : Accumulator<StringValue, StringStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            '"' -> {

                val newStructureStack = structureStack.dropLast(1)

                val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

                when (newPreviousStructure) {
                    is EmptyStructureElement -> {

                        val closeStringElement = StringClose(id = previousClosable.id)

                        Success<String, Accumulator<*, *>>(StringCloseEmptyAccumulator(
                                idCounter = idCounter,
                                previousElement = closeStringElement,
                                structure = structure.plus(closeStringElement)
                        ))
                    }
                    is LiteralStructureElement -> fail("How do I get rid of you as a possibility")
                    is StringStructureElement -> fail("How do I get rid of you as a possibility")
                    is ArrayStructureElement -> {

                        val closeStringElement = StringClose(id = previousClosable.id)

                        Success<String, Accumulator<*, *>>(StringCloseArrayAccumulator(
                                idCounter = idCounter,
                                previousElement = closeStringElement,
                                structure = structure.plus(closeStringElement),
                                previousClosable = newPreviousStructure,
                                structureStack = newStructureStack
                        ))
                    }
                    is ObjectStructureElement -> TODO()
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

data class StringEscapeAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: StringEscape,
        override val previousClosable: StringStructureElement
) : Accumulator<StringEscape, StringStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            '"', '\\', '/' -> {

                val stringElement = StringValue(id = previousClosable.id, value = char)

                Success<String, Accumulator<*, *>>(StringValueAccumulator(
                        idCounter = idCounter,
                        structureStack = structureStack,
                        previousElement = stringElement,
                        structure = structure.plus(stringElement),
                        previousClosable = previousClosable
                ))
            }
            else -> fail("only quotes and slashes may follow escape characters")
        }
    }
}

data class StringCloseEmptyAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val previousElement: StringClose
) : EmptyAccumulator<StringClose>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            else -> fail("nothing can follow a closed root string")
        }
    }
}

data class ArrayOpenAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousClosable: ArrayStructureElement,
        override val previousElement: ArrayOpen
) : Accumulator<ArrayOpen, ArrayStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ']' -> {

                val newStructureStack = structureStack.dropLast(1)

                val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

                when (newPreviousStructure) {

                    is EmptyStructureElement -> {

                        val closeElement = ArrayClose(id = previousClosable.id)

                        Success<String, Accumulator<*, *>>(ArrayCloseEmptyAccumulator(
                                idCounter = idCounter,
                                previousElement = closeElement,
                                structure = structure.plus(closeElement)
                        ))
                    }
                    is LiteralStructureElement -> fail("How do I get rid of you as a possibility")
                    is StringStructureElement -> fail("How do I get rid of you as a possibility")
                    is ArrayStructureElement -> {

                        val closeElement = ArrayClose(id = previousClosable.id)

                        Success<String, Accumulator<*, *>>(ArrayCloseArrayAccumulator(
                                idCounter = idCounter,
                                previousElement = closeElement,
                                structure = structure.plus(closeElement),
                                structureStack = newStructureStack,
                                previousClosable = newPreviousStructure
                        ))
                    }
                    is ObjectStructureElement -> TODO()
                }
            }
            ' ', '\n', '\r', '\t' -> unmodified
            '"' -> {

                val stringStructureElement = StringStructureElement(id = idCounter)

                val openStringElement = StringOpen(id = idCounter)

                Success(StringOpenAccumulator(
                        idCounter = idCounter + 1,
                        structureStack = structureStack.plus(stringStructureElement),
                        previousElement = openStringElement,
                        previousClosable = stringStructureElement,
                        structure = structure.plus(openStringElement)
                ))
            }
            '[' -> {

                val arrayOpen = ArrayOpen(id = idCounter)

                val arrayStructure = ArrayStructureElement(id = idCounter)

                Success(ArrayOpenAccumulator(
                        idCounter = idCounter + 1,
                        structureStack = structureStack.plus(arrayStructure),
                        previousElement = arrayOpen,
                        previousClosable = arrayStructure,
                        structure = structure.plus(arrayOpen)
                ))
            }
            else -> {

                val literalElement = LiteralStructureElement(id = idCounter)

                val literalChild = LiteralValue(value = char, id = idCounter)

                Success(LiteralValueAccumulator(
                        idCounter = idCounter + 1,
                        structureStack = structureStack.plus(literalElement),
                        previousElement = literalChild,
                        previousClosable = literalElement,
                        structure = structure.plus(literalChild)
                ))
            }
        }
    }
}

data class ArrayCommaAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousClosable: ArrayStructureElement,
        override val previousElement: ArrayComma
) : Accumulator<ArrayComma, ArrayStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            '"' -> {

                val stringStructureElement = StringStructureElement(id = idCounter)

                val openStringElement = StringOpen(id = idCounter)

                Success(StringOpenAccumulator(
                        idCounter = idCounter + 1,
                        structureStack = structureStack.plus(stringStructureElement),
                        previousElement = openStringElement,
                        previousClosable = stringStructureElement,
                        structure = structure.plus(openStringElement)
                ))
            }
            '[' -> {

                val arrayOpen = ArrayOpen(id = idCounter)

                val arrayStructure = ArrayStructureElement(id = idCounter)

                Success(ArrayOpenAccumulator(
                        idCounter = idCounter + 1,
                        structureStack = structureStack.plus(arrayStructure),
                        previousElement = arrayOpen,
                        previousClosable = arrayStructure,
                        structure = structure.plus(arrayOpen)
                ))
            }
            else -> {

                val literalElement = LiteralStructureElement(id = idCounter)

                val literalChild = LiteralValue(value = char, id = idCounter)

                Success(LiteralValueAccumulator(
                        idCounter = idCounter + 1,
                        structureStack = structureStack.plus(literalElement),
                        previousElement = literalChild,
                        previousClosable = literalElement,
                        structure = structure.plus(literalChild)
                ))
            }
        }
    }
}

data class LiteralCloseArrayAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val previousElement: LiteralClose,
        override val structureStack: List<MainStructure>,
        override val previousClosable: ArrayStructureElement
) : Accumulator<LiteralClose, ArrayStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            ']' -> {

                val newStructureStack = structureStack.dropLast(1)

                val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

                when (newPreviousStructure) {
                    is EmptyStructureElement -> {

                        val closeElement = ArrayClose(id = previousClosable.id)

                        Success<String, Accumulator<*, *>>(ArrayCloseEmptyAccumulator(
                                idCounter = idCounter,
                                previousElement = closeElement,
                                structure = structure.plus(closeElement)
                        ))
                    }
                    is LiteralStructureElement -> fail("How do I get rid of you as a possibility")
                    is StringStructureElement -> fail("How do I get rid of you as a possibility")
                    is ArrayStructureElement -> TODO()
                    is ObjectStructureElement -> TODO()
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

data class StringCloseArrayAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val previousElement: StringClose,
        override val structureStack: List<MainStructure>,
        override val previousClosable: ArrayStructureElement
) : Accumulator<StringClose, ArrayStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            ']' -> {

                val newStructureStack = structureStack.dropLast(1)

                val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

                when (newPreviousStructure) {
                    is EmptyStructureElement -> {

                        val closeElement = ArrayClose(id = previousClosable.id)

                        Success<String, Accumulator<*, *>>(ArrayCloseEmptyAccumulator(
                                idCounter = idCounter,
                                previousElement = closeElement,
                                structure = structure.plus(closeElement)
                        ))
                    }
                    is LiteralStructureElement -> fail("How do I get rid of you as a possibility")
                    is StringStructureElement -> fail("How do I get rid of you as a possibility")
                    is ArrayStructureElement -> {

                        val closeElement = ArrayClose(id = previousClosable.id)

                        Success<String, Accumulator<*, *>>(ArrayCloseArrayAccumulator(
                                idCounter = idCounter,
                                previousElement = closeElement,
                                structure = structure.plus(closeElement),
                                structureStack = newStructureStack,
                                previousClosable = newPreviousStructure
                        ))
                    }
                    is ObjectStructureElement -> TODO()
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

data class ArrayCloseEmptyAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val previousElement: ArrayClose
) : EmptyAccumulator<ArrayClose>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            ']' -> TODO()
            else -> TODO()
        }
    }
}

data class ArrayCloseArrayAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val previousElement: ArrayClose,
        override val structureStack: List<MainStructure>,
        override val previousClosable: ArrayStructureElement
) : Accumulator<ArrayClose, ArrayStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            ']' -> {

                val newStructureStack = structureStack.dropLast(1)

                val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

                when (newPreviousStructure) {

                    is EmptyStructureElement -> {

                        val closeElement = ArrayClose(id = previousClosable.id)

                        Success<String, Accumulator<*, *>>(ArrayCloseEmptyAccumulator(
                                idCounter = idCounter,
                                previousElement = closeElement,
                                structure = structure.plus(closeElement)
                        ))
                    }
                    is LiteralStructureElement -> fail("How do I get rid of you as a possibility")
                    is StringStructureElement -> fail("How do I get rid of you as a possibility")
                    is ArrayStructureElement -> {

                        val closeElement = ArrayClose(id = previousClosable.id)

                        Success<String, Accumulator<*, *>>(ArrayCloseArrayAccumulator(
                                idCounter = idCounter,
                                previousElement = closeElement,
                                structure = structure.plus(closeElement),
                                structureStack = newStructureStack,
                                previousClosable = newPreviousStructure
                        ))
                    }
                    is ObjectStructureElement -> TODO()
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