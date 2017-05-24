package com.hcsc.de.claims.jsonParsing

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.helpers.flatMap

class LinearJsonStructureParser : JsonStructureParser {

    override fun parse(input: String): Result<String, List<JsonStructureElement>> {

        return input.toCharArray().fold(defaultAccumulator()) { accumulatorResult, char ->

            accumulatorResult.flatMap { structureAccumulator -> structureAccumulator.add(char) }

        }.flatMap { accumulator ->

            Success<String, List<JsonStructureElement>>(accumulator.structure)
        }
    }

    private fun JsonStructureAccumulator.add(char: Char): Result<String, JsonStructureAccumulator> {

        return when (lastClosable) {
            null -> when (previousElement) {
                null -> when (char) {
                    ' ', '\n', '\t', '\r' -> addWhitespace()
                    '}', ']', ',', ':', '\\', '/' -> fail("'$char' may not begin a JSON document")
                    '"' -> startString()
                    '{' -> startObject()
                    '[' -> startArray()
                    else -> addLiteral(char)
                }
                is Literal -> when (char) {
                    ' ', '\n', '\t', '\r' -> addWhitespace()
                    '\"', '{', '}', '[', ']', ',', ':', '\\', '/' -> fail("'$char' may not follow a literal character in a JSON document")
                    else -> addLiteral(char)
                }
                is StringElement, is ObjectElement, is ArrayElement -> when (char) {
                    ' ', '\n', '\t', '\r' -> addWhitespace()
                    else -> fail("'$char' may not follow a closable end at the end of a JSON document")
                }
                // TODO the rest of these should never happen
                CommaElement -> TODO()
                ColonElement -> TODO()
                Escape -> TODO()
                is ElementStart -> TODO()
                is ElementEnd -> TODO()
                is ObjectKeyElement -> TODO()
                is ObjectValueElement -> TODO()
                is ArrayChildElement -> TODO()
            }
            is StringElement -> when (previousElement) {
                is Literal -> when (char) {
                    '"' -> endClosable(lastClosable)
                    '\\' -> addEscape()
                    else -> addLiteral(char)
                }
                Escape -> when (char) {
                    '\\', '"', '/' -> addLiteral(char)
                    else -> fail("'$char' may not follow an escape character")
                }
                is StringElement -> when (char) {
                    '\\' -> addEscape()
                    '"' -> endClosable(lastClosable)
                    else -> addLiteral(char)
                }
                // TODO the rest of these should never happen
                CommaElement -> TODO()
                ColonElement -> TODO()
                is ElementStart -> TODO()
                is ElementEnd -> TODO()
                is ObjectElement -> TODO()
                is ArrayElement -> TODO()
                is ObjectKeyElement -> TODO()
                is ObjectValueElement -> TODO()
                is ArrayChildElement -> TODO()
                null -> TODO()
            }
            is ObjectElement -> when (previousElement) {
                is Literal -> TODO()
                CommaElement -> TODO()
                ColonElement -> TODO()
                Escape -> TODO()
                is ElementStart -> TODO()
                is ElementEnd -> TODO()
                is StringElement -> TODO()
                is ObjectElement -> endClosable(lastClosable)
                is ArrayElement -> TODO()
                is ObjectValueElement -> TODO()
                is ArrayChildElement -> TODO()
                is ObjectKeyElement -> TODO()
                null -> TODO()
            }
            is ArrayElement -> when (previousElement) {
                is ArrayElement -> when (char) {
                    ']' -> endClosable(lastClosable)
                    '"' -> startStringArrayChild()
                    else -> TODO()
                }
                is ArrayChildElement -> when (char) {
                    ']' -> endClosable(lastClosable)
                    else -> TODO()
                }
                is Literal -> TODO()
                CommaElement -> TODO()
                ColonElement -> TODO()
                Escape -> TODO()
                is ElementStart -> TODO()
                is ElementEnd -> TODO()
                is StringElement -> TODO()
                is ObjectElement -> TODO()
                is ObjectValueElement -> TODO()
                is ObjectKeyElement -> TODO()
                null -> TODO()
            }
            is ArrayChildElement -> when (previousElement) {
                is ArrayChildElement -> when (char) {
                    '"' -> endClosable(lastClosable)
                    else -> TODO()
                }
                is Literal -> TODO()
                CommaElement -> TODO()
                ColonElement -> TODO()
                Escape -> TODO()
                is ElementStart -> TODO()
                is ElementEnd -> TODO()
                is StringElement -> TODO()
                is ObjectElement -> TODO()
                is ArrayElement -> TODO()
                is ObjectValueElement -> TODO()
                is ObjectKeyElement -> TODO()
                null -> TODO()
            }
            is ObjectKeyElement -> TODO()
            is ObjectValueElement -> TODO()
        }
    }

    private fun defaultAccumulator(): Result<String, JsonStructureAccumulator> = Success(JsonStructureAccumulator())
}