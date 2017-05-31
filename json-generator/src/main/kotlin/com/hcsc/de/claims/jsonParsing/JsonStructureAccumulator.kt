package com.hcsc.de.claims.jsonParsing

import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success

internal data class JsonStructureAccumulator(
        val idCounter: Long = 0,
        val closableStack: List<ClosableElement> = emptyList(),
        val previousElement: NotWhitespaceElement? = null,
        val structure: List<JsonStructureElement> = emptyList()
) {

    val lastClosable = closableStack.lastOrNull()

    fun fail(message: String) = Failure<String, JsonStructureAccumulator>("Invalid JSON: $message")

    fun addSimpleElement(element: SimpleElement) = Success<String, JsonStructureAccumulator>(copy(structure = structure.plus(element)).updatePreviousElement())

    fun addLiteral(char: Char) = addSimpleElement(Literal(char))

    fun addColon() = addSimpleElement(ColonElement)

    fun addComma() = addSimpleElement(CommaElement)

    fun addEscape() = addSimpleElement(Escape)

    fun addWhitespace() = Success<String, JsonStructureAccumulator>(copy(structure = structure.plus(Whitespace)))

    fun startClosable(newClosableConstructor: (id: Long) -> ClosableElement): Result<String, JsonStructureAccumulator> {

        val newClosableElement = newClosableConstructor.invoke(idCounter)

        return Success(copy(
                structure = structure.plus(ElementStart(newClosableElement)),
                closableStack = closableStack.plus(newClosableElement),
                idCounter = idCounter + 1
        ).updatePreviousElement())
    }

    fun startString() = startClosable(::StringElement)

    fun startObject() = startClosable(::ObjectElement)

    fun startArray() = startClosable(::ArrayElement)

    fun startArrayChild(newClosableConstructor: (id: Long) -> ClosableValueElement): Result<String, JsonStructureAccumulator> {

        val newClosableElement = ArrayChildElement(newClosableConstructor.invoke(idCounter))

        return Success(copy(
                structure = structure.plus(ElementStart(newClosableElement)),
                closableStack = closableStack.plus(newClosableElement),
                idCounter = idCounter + 1
        ).updatePreviousElement())
    }

    fun startStringArrayChild() = startArrayChild(::StringElement)

    fun startObjectValue(newClosableConstructor: (id: Long) -> ClosableValueElement): JsonStructureAccumulator {

        val newClosableElement = ObjectValueElement(newClosableConstructor.invoke(idCounter))

        return copy(
                structure = structure.plus(ElementStart(newClosableElement)),
                closableStack = closableStack.plus(newClosableElement),
                idCounter = idCounter + 1
        ).updatePreviousElement()
    }

    fun endClosable(closable: ClosableElement): Result<String, JsonStructureAccumulator> =
            Success(copy(
                    structure = structure.plus(ElementEnd(closable)),
                    closableStack = closableStack.dropLast(1)
            ).updatePreviousElement())

    private fun updatePreviousElement(): JsonStructureAccumulator {
        val lastElement = structure.lastOrNull()

        // TODO maybe test this explicitly
        return when (lastElement) {
            is ElementWrapper -> copy(previousElement = lastElement.element)
            is CloseableWrapper -> copy(previousElement = lastElement.element)
            is NotWhitespaceElement -> copy(previousElement = lastElement)
            Whitespace, null -> this
        }
    }
}
