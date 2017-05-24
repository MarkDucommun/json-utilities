package com.hcsc.de.claims.jsonParsing

import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success

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
                                    is ObjectValueElement -> TODO()
                                    else -> acc.startObjectValue(::StringElement)
                                }
                            }
                            else -> acc.addLiteral(char)
                        }
                    }
                    is ElementStart -> {
                        when (acc.previousElement.element) {
                            is ObjectElement -> when (char) {
                                '"' -> when (lastClosable) {
                                    is StringElement, is ObjectKeyElement -> acc.endClosable(lastClosable)
                                    is ObjectElement -> acc.startClosable(::ObjectKeyElement)
                                    is ObjectValueElement -> {
                                        when (lastClosable.element) {
                                            is StringElement -> acc.endClosable(lastClosable)
                                            else -> TODO()
                                        }
                                    }
                                    else -> acc.startClosable(::StringElement)
                                }
                                else -> acc.fail("object keys must be strings")
                            }
                            else -> acc.addLiteral(char)
                        }
                    }
                    else -> {
                        // TODO previous element is not a Start, comma or colon should be a failure
                        when (char) {
                            '"' -> {
                                when (lastClosable) {
                                    is StringElement, is ObjectKeyElement -> acc.endClosable(lastClosable)
                                    is ObjectElement -> acc.startClosable(::ObjectKeyElement)
                                    is ObjectValueElement -> {
                                        when (lastClosable.element) {
                                            is StringElement -> acc.endClosable(lastClosable)
                                            else -> TODO()
                                        }
                                    }
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
                        // TODO previous element is not a start, comma or colon or beginning of string  should be a failure
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
                                    is ObjectElement -> acc.addComma()
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
                            else -> acc.addLiteral(char)
                        }
                    }
                }.let { accumulator ->

                    val newlyAddedElement = accumulator.structure.lastOrNull() ?: Literal(' ')

                    when (newlyAddedElement) {
                        is NotWhitespaceElement -> accumulator.copy(previousElement = newlyAddedElement)
                        else -> accumulator
                    }
                }
            }
        }

        return accumulator.failure ?: {

            if (accumulator.closableStack.isEmpty()) {

                Success<String, List<JsonStructureElement>>(accumulator.structure)
            } else {
                when (accumulator.closableStack.lastOrNull()) {
                    is StringElement -> accumulator.fail("did not close string").failure!!
                    is ObjectElement -> TODO()
                    is ArrayElement -> TODO()
                    is ObjectKeyElement -> TODO()
                    is ObjectValueElement -> TODO()
                    is ArrayChildElement -> TODO()
                    null -> TODO("I don't think that this is at all possible")
                }
            }
        }()
    }
}