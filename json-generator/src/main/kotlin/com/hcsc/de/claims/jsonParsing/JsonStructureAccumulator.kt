package com.hcsc.de.claims.jsonParsing

import com.hcsc.de.claims.helpers.Failure

internal data class JsonStructureAccumulator(
        val idCounter: Long = 0,
        val closableStack: List<ClosableElement> = emptyList(),
        val previousElement: NotWhitespaceElement = Literal(' '), // TODO might this actually be nullable?
        val structure: List<JsonStructureElement> = emptyList(),
        val failure: Failure<String, List<JsonStructureElement>>? = null
) {

    fun fail(message: String) = copy(failure = Failure("Invalid JSON, $message"))

    fun addSimpleElement(element: SimpleElement) = copy(structure = structure.plus(element))

    fun addLiteral(char: Char) = addSimpleElement(Literal(char))

    fun addColon() = addSimpleElement(ColonElement)

    fun addComma() = addSimpleElement(CommaElement)

    fun startClosable(newClosableConstructor: (id: Long) -> ClosableElement): JsonStructureAccumulator {

        val newClosableElement = newClosableConstructor.invoke(idCounter)

        return copy(
                structure = structure.plus(ElementStart(newClosableElement)),
                closableStack = closableStack.plus(newClosableElement),
                idCounter = idCounter + 1
        )
    }

    fun startObjectValue(newClosableConstructor: (id: Long) -> ClosableValueElement): JsonStructureAccumulator {

        val newClosableElement = ObjectValueElement(newClosableConstructor.invoke(idCounter))

        return copy(
                structure = structure.plus(ElementStart(newClosableElement)),
                closableStack = closableStack.plus(newClosableElement),
                idCounter = idCounter + 1
        )
    }

    fun endClosable(closable: ClosableElement): JsonStructureAccumulator = copy(
            structure = structure.plus(ElementEnd(closable)),
            closableStack = closableStack.dropLast(1)
    )
}
