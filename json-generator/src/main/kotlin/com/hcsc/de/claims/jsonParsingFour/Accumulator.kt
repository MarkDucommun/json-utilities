package com.hcsc.de.claims.jsonParsingFour

import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success

sealed class Accumulator<out previousElementType : JsonStructureElement, out previousClosableType : MainStructureElement?> {

    abstract val idCounter: Long

    abstract val structure: List<JsonStructureElement>

    abstract val structureStack: List<MainStructureElement>

    abstract val previousClosable: previousClosableType

    abstract val previousElement: previousElementType

    abstract fun processChar(char: Char): Result<String, Accumulator<*, *>>

    fun fail(message: String): Failure<String, Accumulator<*, *>> = Failure("Invalid JSON - $message")

    val unmodified: Success<String, Accumulator<*, *>> get() = Success(this)
}

object RootAccumulator : Accumulator<EmptyStructureElement, EmptyStructureElement>() {

    override val idCounter: Long = 1

    override val structure: List<JsonStructureElement> = emptyList()

    override val previousElement: EmptyStructureElement = EmptyStructureElement

    override val structureStack: List<MainStructureElement> = listOf(EmptyStructureElement)

    override val previousClosable: EmptyStructureElement = EmptyStructureElement

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            '"' -> {

                val stringStructureElement = StringStructureElement(id = idCounter)

                val openStringElement = StringChildOpenElement(id = idCounter)

                Success(StringChildOpenJsonAccumulator(
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

                val literalChild = LiteralChildStructureElement(value = char, id = idCounter)

                Success(LiteralChildAccumulator(
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

data class LiteralChildAccumulator(
        override val idCounter: Long,
        override val structureStack: List<MainStructureElement>,
        override val previousClosable: LiteralStructureElement,
        override val previousElement: LiteralChildStructureElement,
        override val structure: List<JsonStructureElement>
) : Accumulator<LiteralChildStructureElement, LiteralStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        val newStructureStack = structureStack.dropLast(1)

        val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

        return when (char) {
            ' ', '\n', '\r', '\t' -> when (newPreviousStructure) {
                EmptyStructureElement -> {

                    val literalChildCloseElement = LiteralChildCloseElement(
                            id = previousElement.id,
                            value = previousElement.value
                    )

                    Success<String, Accumulator<*, *>>(LiteralChildCloseEmptyAccumulator(
                            idCounter = previousClosable.id,
                            previousElement = literalChildCloseElement,
                            structure = structure.dropLast(1).plus(literalChildCloseElement)
                    ))
                }
                is LiteralStructureElement -> TODO("THIS REALLY SHOULD NEVER HAPPEN")
                is StringStructureElement -> TODO()
                is ArrayStructureElement -> {

                    val literalChildCloseElement = LiteralChildCloseElement(
                            id = previousElement.id,
                            value = previousElement.value
                    )

                    Success<String, Accumulator<*, *>>(LiteralCloseArrayAccumulator(
                            idCounter = previousClosable.id,
                            previousElement = literalChildCloseElement,
                            previousClosable = newPreviousStructure,
                            structure = structure.dropLast(1).plus(literalChildCloseElement),
                            structureStack = newStructureStack
                    ))
                }
            }
            ',' -> when (newPreviousStructure) {
                is ArrayStructureElement -> {

                    val arrayClose = ArrayComma(newPreviousStructure.id)

                    Success<String, Accumulator<*, *>>(ArrayCommaArrayAccumulator(
                            idCounter = idCounter,
                            structure = structure.dropLast(1)
                                    .plus(LiteralChildCloseElement(id = previousElement.id, value = previousElement.value))
                                    .plus(arrayClose),
                            previousElement = arrayClose,
                            structureStack = 
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
                                            .plus(LiteralChildCloseElement(id = previousElement.id, value = previousElement.value))
                                            .plus(arrayClose),
                                    previousElement = arrayClose
                            ))
                        }
                        is LiteralStructureElement -> TODO()
                        is StringStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                        is ArrayStructureElement -> TODO()
                    }
                }
                else -> TODO()
            }
            else -> {

                val literalChild = LiteralChildStructureElement(value = char, id = previousClosable.id)

                Success(LiteralChildAccumulator(
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

data class LiteralChildCloseEmptyAccumulator(
        override val idCounter: Long,
        override val previousElement: LiteralChildCloseElement,
        override val structure: List<JsonStructureElement>
) : EmptyAccumulator<LiteralChildCloseElement>() {

    override val structureStack: List<MainStructureElement> = listOf(EmptyStructureElement)

    override val previousClosable: EmptyStructureElement = EmptyStructureElement

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            else -> fail("nothing can follow a closed root literal")
        }
    }
}

data class StringChildOpenJsonAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructureElement>,
        override val structureStack: List<MainStructureElement>,
        override val previousElement: StringChildOpenElement,
        override val previousClosable: StringStructureElement
) : Accumulator<StringChildOpenElement, StringStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            '"' -> {

                val newStructureStack = structureStack.dropLast(1)

                val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

                when (newPreviousStructure) {

                    is EmptyStructureElement -> {

                        val closeStringElement = StringChildCloseElement(id = previousClosable.id)

                        Success<String, Accumulator<*, *>>(StringChildCloseEmptyJsonAccumulator(
                                idCounter = idCounter,
                                previousElement = closeStringElement,
                                structure = structure.plus(closeStringElement)
                        ))
                    }
                    is LiteralStructureElement -> fail("How do I get rid of you as a possibility")
                    is StringStructureElement -> fail("How do I get rid of you as a possibility")
                    is ArrayStructureElement -> fail("How do I get rid of you as a possibility")
                }
            }
            '\\' -> Success<String, Accumulator<*, *>>(StringEscapeJsonAccumulator(
                    idCounter = idCounter,
                    structureStack = structureStack,
                    previousElement = StringEscape,
                    structure = structure,
                    previousClosable = previousClosable
            ))
            else -> {

                val stringElement = StringChildStructureElement(id = previousClosable.id, value = char)

                Success<String, Accumulator<*, *>>(StringChildStructureJsonAccumulator(
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

data class StringChildStructureJsonAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructureElement>,
        override val structureStack: List<MainStructureElement>,
        override val previousElement: StringChildStructureElement,
        override val previousClosable: StringStructureElement
) : Accumulator<StringChildStructureElement, StringStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            '"' -> {

                val newStructureStack = structureStack.dropLast(1)

                val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

                when (newPreviousStructure) {

                    is EmptyStructureElement -> {

                        val closeStringElement = StringChildCloseElement(id = previousClosable.id)

                        Success<String, Accumulator<*, *>>(StringChildCloseEmptyJsonAccumulator(
                                idCounter = idCounter,
                                previousElement = closeStringElement,
                                structure = structure.plus(closeStringElement)
                        ))
                    }
                    is LiteralStructureElement -> fail("How do I get rid of you as a possibility")
                    is StringStructureElement -> fail("How do I get rid of you as a possibility")
                    is ArrayStructureElement -> fail("How do I get rid of you as a possibility")
                }
            }
            '\\' -> Success<String, Accumulator<*, *>>(StringEscapeJsonAccumulator(
                    idCounter = idCounter,
                    structureStack = structureStack,
                    previousElement = StringEscape,
                    structure = structure,
                    previousClosable = previousClosable
            ))
            else -> {

                val stringElement = StringChildStructureElement(id = previousClosable.id, value = char)

                Success<String, Accumulator<*, *>>(StringChildStructureJsonAccumulator(
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

data class StringEscapeJsonAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructureElement>,
        override val structureStack: List<MainStructureElement>,
        override val previousElement: StringEscape,
        override val previousClosable: StringStructureElement
) : Accumulator<StringEscape, StringStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            '"', '\\', '/' -> {

                val stringElement = StringChildStructureElement(id = previousClosable.id, value = char)

                Success<String, Accumulator<*, *>>(StringChildStructureJsonAccumulator(
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

data class StringChildCloseJsonAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructureElement>,
        override val structureStack: List<MainStructureElement>,
        override val previousElement: StringChildCloseElement,
        override val previousClosable: StringStructureElement
) : Accumulator<StringChildCloseElement, StringStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            else -> TODO()
        }
    }
}

sealed class EmptyAccumulator<out previousElementType : JsonStructureElement> : Accumulator<previousElementType, EmptyStructureElement>() {

    override val previousClosable: EmptyStructureElement = EmptyStructureElement

    override val structureStack: List<MainStructureElement> = listOf(EmptyStructureElement)
}

data class StringChildCloseEmptyJsonAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructureElement>,
        override val previousElement: StringChildCloseElement
) : EmptyAccumulator<StringChildCloseElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            else -> fail("nothing can follow a closed root string")
        }
    }
}

data class ArrayOpenAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructureElement>,
        override val structureStack: List<MainStructureElement>,
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
                    is ArrayStructureElement -> TODO()
                }
            }
            ' ', '\n', '\r', '\t' -> unmodified
            else -> {

                val literalElement = LiteralStructureElement(id = idCounter)

                val literalChild = LiteralChildStructureElement(value = char, id = idCounter)

                Success(LiteralChildAccumulator(
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

data class ArrayCommaArrayAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructureElement>,
        override val structureStack: List<MainStructureElement>,
        override val previousClosable: ArrayStructureElement,
        override val previousElement: ArrayOpen
) : Accumulator<ArrayComma, ArrayStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            else -> {

                val literalElement = LiteralStructureElement(id = idCounter)

                val literalChild = LiteralChildStructureElement(value = char, id = idCounter)

                Success(LiteralChildAccumulator(
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

data class ArrayCloseEmptyAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructureElement>,
        override val previousElement: ArrayClose
) : EmptyAccumulator<ArrayClose>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            else -> TODO()
        }
    }
}

data class LiteralCloseArrayAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructureElement>,
        override val previousElement: LiteralChildCloseElement,
        override val structureStack: List<MainStructureElement>,
        override val previousClosable: ArrayStructureElement
) : Accumulator<LiteralChildCloseElement, ArrayStructureElement>() {

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
                }
            }
            else -> TODO()
        }
    }
}