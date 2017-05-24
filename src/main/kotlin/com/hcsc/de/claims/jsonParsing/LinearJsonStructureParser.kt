package com.hcsc.de.claims.jsonParsing

import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import java.lang.reflect.Constructor
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class LinearJsonStructureParser : JsonStructureParser {

    override fun parse(input: String): Result<String, List<JsonStructureElement>> {

        val accumulator = input.toCharArray().fold(JsonStructureAccumulator()) { acc, char ->

            if (acc.failure != null) {

                acc

            } else {

                val lastClosable = acc.closableStack.lastOrNull()

                when (acc.previousElement) {
                    is ColonElement -> {
                        when (char) {
                            '"' -> {
                                when (lastClosable) {
                                    is ObjectValueElement -> {
                                        TODO()
                                    }
                                    else -> {
                                        val newObjectValue = ObjectValueElement(StringElement(id = acc.idCounter))
                                        acc.copy(
                                                structure = acc.structure.plus(ElementStart(newObjectValue)),
                                                closableStack = acc.closableStack.plus(newObjectValue),
                                                idCounter = acc.idCounter + 1
                                        )
                                    }
                                }
                            }
                            else -> acc.addLiteral()
                        }
                    }
                    else -> {
                        // TODO previous element is not a Start, comma or colon should be a failure
                        when (char) {
                            '"' -> {
                                when (lastClosable) {
                                    is StringElement, is ObjectKeyElement -> acc.endClosable(lastClosable)
                                    is ObjectElement -> acc.startClosable(::ObjectKeyElement)
                                    else -> acc.startClosable(::StringElement)
                                }
                            }
                        // TODO previous element is not a Start, comma or colon or beginning of string should be a failure
                            '[' -> acc.startClosable(::ArrayElement)
                            ']' -> {
                                when (lastClosable) {
                                    is ArrayElement -> acc.endClosable(lastClosable)
                                    is ObjectElement -> acc.fail("attempted to close array before object was closed")
                                    is StringElement -> acc.fail("attempted to close array before string was closed")
                                    is ObjectKeyElement -> TODO()
                                    is ObjectValueElement -> TODO()
                                    is ArrayChildElement -> TODO()
                                    null -> acc.fail("array was never opened")
                                }
                            }
                        // TODO previous element is not a start, comma or colonor beginning of string  should be a failure
                            '{' -> acc.startClosable(::ObjectElement)
                            '}' -> {
                                when (lastClosable) {
                                    is ObjectElement -> acc.endClosable(lastClosable)
                                    is ArrayElement -> acc.fail("attempted to close object before array was closed")
                                    is StringElement -> acc.fail("attempted to close object before string was closed")
                                    is ObjectKeyElement -> TODO()
                                    is ObjectValueElement -> TODO()
                                    is ArrayChildElement -> TODO()
                                    null -> acc.fail("object was never opened")
                                }
                            }
                            ',' -> {
                                when (lastClosable) {
                                    is ArrayElement -> acc.addComma()
                                    null -> acc.fail("cannot use comma outside of an object, string or array")
                                    else -> TODO()
                                }
                            }
                            ':' -> {
                                when (lastClosable) {
                                    is ObjectElement -> acc.addColon()
                                    null -> acc.fail("cannot use colon outside of an object")
                                    else -> TODO()
                                }
                            }
                            else -> acc.addLiteral()
                        }
                    }
                }.let { accumulator ->

                    val newlyAddedElement = accumulator.structure.lastOrNull() ?: Literal

                    when (newlyAddedElement) {
                        is NotWhitespaceElement -> accumulator.copy(previousElement = newlyAddedElement)
                        else -> accumulator
                    }
                }
            }
        }

        return accumulator.failure ?: Success(accumulator.structure)
    }

    data class JsonStructureAccumulator(
            val idCounter: Long = 0,
            val closableStack: List<ClosableElement> = emptyList(),
            val previousElement: NotWhitespaceElement = Literal, // TODO might this actually be nullable?
            val structure: List<JsonStructureElement> = emptyList(),
            val failure: Failure<String, List<JsonStructureElement>>? = null
    ) {

        fun fail(message: String) = copy(failure = Failure("Invalid JSON, $message"))

        fun addSimpleElement(element: SimpleElement) = copy(structure = structure.plus(element))

        fun addLiteral() = addSimpleElement(Literal)

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

        fun endClosable(closable: ClosableElement): JsonStructureAccumulator {

            return copy(
                    structure = structure.plus(ElementEnd(closable)),
                    closableStack = closableStack.dropLast(1)
            )
        }
    }
}