package com.hcsc.de.claims.jsonParsingFour

import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success

interface AccumulatorInt<out previousElementType : JsonStructure, out previousClosableType : MainStructure?> {

    val idCounter: Long

    val structure: List<JsonStructure>

    val structureStack: List<MainStructure>

    val previousClosable: previousClosableType

    val previousElement: previousElementType

    fun processChar(char: Char): Result<String, Accumulator<*, *>>
}

sealed class Accumulator<out previousElementType : JsonStructure, out previousClosableType : MainStructure?>
    : AccumulatorInt<previousElementType, previousClosableType> {

    fun openString(): Result<String, Accumulator<*, *>> {

        return openStructure(::StringOpen, ::StringStructureElement, ::StringOpenAccumulator)
    }

    fun openArray(): Result<String, Accumulator<*, *>> {

        return openStructure(::ArrayOpen, ::ArrayStructureElement, ::ArrayOpenAccumulator)
    }

    fun openObject(): Result<String, Accumulator<*, *>> {

        return openStructure(::ObjectOpen, ::OpenObjectStructure, ::ObjectOpenAccumulator)
    }

    fun openLiteral(char: Char): Result<String, Accumulator<*, *>> {
        return openStructure(
                LiteralValue(id = idCounter, value = char),
                ::LiteralStructureElement,
                ::LiteralValueAccumulator
        )
    }

    fun <T : JsonStructure, U : MainStructure> openStructure(
            elementConstructor: (Long) -> T,
            structureConstructor: (Long) -> U,
            accumulatorConstructor: (Long, List<JsonStructure>, List<MainStructure>, T, U) -> Accumulator<T, U>
    ): Result<String, Accumulator<*, *>> {

        return openStructure(elementConstructor(idCounter), structureConstructor, accumulatorConstructor)
    }

    fun <T : JsonStructure, U : MainStructure> openStructure(
            element: T,
            structureConstructor: (Long) -> U,
            accumulatorConstructor: (Long, List<JsonStructure>, List<MainStructure>, T, U) -> Accumulator<T, U>
    ): Result<String, Accumulator<*, *>> {

        return structureConstructor(idCounter).let { newStructure ->

            Success(accumulatorConstructor(
                    idCounter + 1,
                    structure.plus(element),
                    structureStack.plus(newStructure),
                    element,
                    newStructure
            ))
        }

    }

    fun fail(message: String): Failure<String, Accumulator<*, *>> = Failure("Invalid JSON - $message")

    val unmodified: Success<String, Accumulator<*, *>> get() = Success(this)
}

sealed class EmptyAccumulator<out previousElementType : JsonStructure> : Accumulator<previousElementType, EmptyStructureElement>() {

    abstract val elementName: String

    override val previousClosable: EmptyStructureElement = EmptyStructureElement

    override val structureStack: List<MainStructure> = listOf(EmptyStructureElement)

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            else -> fail("nothing can follow a closed root $elementName")
        }
    }
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
            '"' -> openString()
            '[' -> openArray()
            '{' -> openObject()
            else -> openLiteral(char)
        }
    }
}

data class LiteralValueAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: LiteralValue,
        override val previousClosable: LiteralStructureElement
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
                            idCounter = idCounter,
                            previousElement = literalChildCloseElement,
                            structure = structure.dropLast(1).plus(literalChildCloseElement)
                    ))
                }
                is LiteralStructureElement -> TODO("THIS REALLY SHOULD NEVER HAPPEN")
                is StringStructureElement -> TODO("THIS REALLY SHOULD NEVER HAPPEN")
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
                is OpenObjectStructure -> TODO()
                is ObjectWithKeyStructure -> {

                    val literalChildCloseElement = LiteralClose(
                            id = previousElement.id,
                            value = previousElement.value
                    )

                    val modifiedNewPreviousStructure = OpenObjectStructure(id = newPreviousStructure.id)

                    Success<String, Accumulator<*, *>>(LiteralCloseOpenObjectAccumulator(
                            idCounter = idCounter,
                            previousElement = literalChildCloseElement,
                            previousClosable = modifiedNewPreviousStructure,
                            structure = structure.dropLast(1).plus(literalChildCloseElement),
                            structureStack = newStructureStack.dropLast(1).plus(modifiedNewPreviousStructure)
                    ))
                }
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
                        is ObjectStructureElement -> {

                            val closeElement = ArrayClose(id = newPreviousStructure.id)

                            val modifiedEvenNewerPreviousStructure = OpenObjectStructure(id = evenNewerPreviousStructure.id)

                            Success<String, Accumulator<*, *>>(ArrayCloseOpenObjectAccumulator(
                                    idCounter = idCounter,
                                    previousElement = closeElement,
                                    structure = structure.dropLast(1)
                                            .plus(LiteralClose(id = previousElement.id, value = previousElement.value))
                                            .plus(closeElement),
                                    structureStack = evenNewerStructureStack.dropLast(1).plus(modifiedEvenNewerPreviousStructure),
                                    previousClosable = modifiedEvenNewerPreviousStructure
                            ))
                        }
                    }
                }
                else -> TODO()
            }
            '}' -> when (newPreviousStructure) {
                is ObjectWithKeyStructure -> {

                    val evenNewerStructureStack = newStructureStack.dropLast(1)

                    val evenNewerPreviousStructure = evenNewerStructureStack.lastOrNull() ?: EmptyStructureElement

                    when (evenNewerPreviousStructure) {
                        EmptyStructureElement -> {

                            val objectClose = ObjectClose(newPreviousStructure.id)

                            Success<String, Accumulator<*, *>>(ObjectCloseEmptyAccumulator(
                                    idCounter = idCounter,
                                    structure = structure.dropLast(1)
                                            .plus(LiteralClose(id = previousElement.id, value = previousElement.value))
                                            .plus(objectClose),
                                    previousElement = objectClose
                            ))
                        }
                        is ArrayStructureElement -> TODO()
                        is ObjectStructureElement -> {

                            val objectClose = ObjectClose(newPreviousStructure.id)

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

    override val elementName: String = "literal"
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
                    is OpenObjectStructure -> {

                        val closeStringElement = StringClose(id = previousClosable.id)

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

                        val closeStringElement = StringClose(id = previousClosable.id)

                        val modifiedNewPreviousStructure = OpenObjectStructure(id = newPreviousStructure.id)

                        Success<String, Accumulator<*, *>>(StringCloseOpenObjectAccumulator(
                                idCounter = idCounter,
                                previousElement = closeStringElement,
                                structure = structure.plus(closeStringElement),
                                previousClosable = modifiedNewPreviousStructure,
                                structureStack = newStructureStack.dropLast(1).plus(modifiedNewPreviousStructure)
                        ))
                    }
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

    override val elementName: String = "string"
}

data class ArrayOpenAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: ArrayOpen,
        override val previousClosable: ArrayStructureElement
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
                    is ObjectWithKeyStructure -> {

                        val closeElement = ArrayClose(id = previousClosable.id)

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
            ' ', '\n', '\r', '\t' -> unmodified
            '"' -> openString()
            '[' -> openArray()
            else -> openLiteral(char)
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
            '"' -> openString()
            '[' -> openArray()
            else -> openLiteral(char)
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
                    is ArrayStructureElement -> {

                        val closeElement = ArrayClose(id = previousClosable.id)

                        Success<String, Accumulator<*, *>>(ArrayCloseArrayAccumulator(
                                idCounter = idCounter,
                                structureStack = newStructureStack,
                                structure = structure.plus(closeElement),
                                previousClosable = newPreviousStructure,
                                previousElement = closeElement
                        ))
                    }
                    is ObjectStructureElement -> {

                        val closeElement = ArrayClose(id = previousClosable.id)

                        val modifiedNewPreviousStructure = OpenObjectStructure(id = newPreviousStructure.id)

                        Success<String, Accumulator<*, *>>(ArrayCloseOpenObjectAccumulator(
                                idCounter = idCounter,
                                structureStack = newStructureStack.dropLast(1).plus(modifiedNewPreviousStructure),
                                structure = structure.plus(closeElement),
                                previousClosable = modifiedNewPreviousStructure,
                                previousElement = closeElement
                        ))
                    }
                    is LiteralStructureElement -> fail("How do I get rid of you as a possibility")
                    is StringStructureElement -> fail("How do I get rid of you as a possibility")
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

    override val elementName: String = "array"
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

data class ObjectOpenAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: ObjectOpen,
        override val previousClosable: OpenObjectStructure
) : Accumulator<ObjectOpen, OpenObjectStructure>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            '"' -> {

                val open = StringOpen(id = idCounter)

                val string = StringStructureElement(id = idCounter)

                Success(StringOpenAccumulator(
                        idCounter = idCounter + 1,
                        structure = structure.plus(open),
                        structureStack = structureStack.plus(string),
                        previousElement = open,
                        previousClosable = string
                ))
            }
            '}' -> {

                val newStructureStack = structureStack.dropLast(1)

                val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

                when (newPreviousStructure) {
                    is EmptyStructureElement -> {

                        val close = ObjectClose(id = previousClosable.id)

                        Success<String, Accumulator<*, *>>(ObjectCloseEmptyAccumulator(
                                idCounter = idCounter,
                                structure = structure.plus(close),
                                previousElement = close
                        ))
                    }
                    is LiteralStructureElement -> TODO()
                    is StringStructureElement -> TODO()
                    is ArrayStructureElement -> TODO()
                    is OpenObjectStructure -> TODO()
                    is ObjectWithKeyStructure -> {

                        val close = ObjectClose(id = previousClosable.id)

                        val modifiedNewPreviousStructure = OpenObjectStructure(id = newPreviousStructure.id)

                        Success<String, Accumulator<*, *>>(ObjectCloseOpenObjectAccumulator(
                                idCounter = idCounter,
                                structure = structure.plus(close),
                                structureStack = newStructureStack,
                                previousElement = close,
                                previousClosable = modifiedNewPreviousStructure
                        ))
                    }
                }
            }
            else -> fail("object key must be a string")
        }
    }
}

data class ObjectWithKeyAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: StringClose,
        override val previousClosable: ObjectWithKeyStructure
) : Accumulator<StringClose, ObjectWithKeyStructure>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            ':' -> {

                val colon = ObjectColon(id = previousClosable.id)

                Success(ObjectReadyForValueAccumulator(
                        idCounter = idCounter,
                        structure = structure.plus(colon),
                        structureStack = structureStack,
                        previousElement = colon,
                        previousClosable = previousClosable
                ))
            }
            else -> fail("colon must follow an object key")
        }
    }
}

data class ObjectReadyForValueAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: ObjectColon,
        override val previousClosable: ObjectWithKeyStructure
) : Accumulator<ObjectColon, ObjectWithKeyStructure>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            '"' -> openString()
            '[' -> openArray()
            '{' -> openObject()
            '}' -> fail("a value must follow a colon")
            else -> openLiteral(char)
        }
    }
}

data class LiteralCloseOpenObjectAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: LiteralClose,
        override val previousClosable: OpenObjectStructure
) : Accumulator<LiteralClose, OpenObjectStructure>() {

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

                val close = ObjectClose(id = previousClosable.id)

                Success(ObjectCloseEmptyAccumulator(
                        idCounter = idCounter,
                        structure = structure.plus(close),
                        previousElement = close
                ))
            }
            else -> TODO()
        }
    }
}

data class StringCloseOpenObjectAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: StringClose,
        override val previousClosable: OpenObjectStructure
) : Accumulator<StringClose, OpenObjectStructure>() {

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

                val close = ObjectClose(id = previousClosable.id)

                Success(ObjectCloseEmptyAccumulator(
                        idCounter = idCounter,
                        structure = structure.plus(close),
                        previousElement = close
                ))
            }
            else -> TODO()
        }
    }
}

data class ArrayCloseOpenObjectAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: ArrayClose,
        override val previousClosable: OpenObjectStructure
) : Accumulator<ArrayClose, OpenObjectStructure>() {

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

                val close = ObjectClose(id = previousClosable.id)

                Success(ObjectCloseEmptyAccumulator(
                        idCounter = idCounter,
                        structure = structure.plus(close),
                        previousElement = close
                ))
            }
            else -> TODO()
        }
    }
}

data class ObjectCloseOpenObjectAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: ObjectClose,
        override val previousClosable: OpenObjectStructure
) : Accumulator<ObjectClose, OpenObjectStructure>() {

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

                val close = ObjectClose(id = previousClosable.id)

                Success<String, Accumulator<*, *>>(ObjectCloseEmptyAccumulator(
                        idCounter = idCounter,
                        structure = structure.plus(close),
                        previousElement = close
                ))
            }
            else -> TODO()
        }
    }
}

data class ObjectCommaAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousClosable: OpenObjectStructure,
        override val previousElement: ObjectComma
) : Accumulator<ObjectComma, OpenObjectStructure>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            '"' -> {

                val open = StringOpen(id = idCounter)

                val string = StringStructureElement(id = idCounter)

                Success(StringOpenAccumulator(
                        idCounter = idCounter + 1,
                        structure = structure.plus(open),
                        structureStack = structureStack.plus(string),
                        previousElement = open,
                        previousClosable = string
                ))
            }
            else -> TODO()
//            else -> fail("object key must be a string")
        }
    }
}

data class ObjectCloseEmptyAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val previousElement: ObjectClose
) : EmptyAccumulator<ObjectClose>() {

    override val elementName: String = "object"
}
