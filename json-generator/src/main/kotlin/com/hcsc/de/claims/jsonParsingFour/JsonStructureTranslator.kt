package com.hcsc.de.claims.jsonParsingFour

import com.hcsc.de.claims.helpers.*

class JsonStructureTranslator {

    fun translate(structure: MainStructure<*>): Result<String, JsonNode> {

        return structure.asNode
    }

    private val MainStructure<*>.asNode: Result<String, JsonNode> get() {

        return when (this) {
            is StringStructureElement -> Success<String, StringNode>(asNode)
            is LiteralStructureElement -> asNode
            is ArrayStructureElement -> asNode
            is OpenObjectStructure -> asNode
            is ObjectWithKeyStructure -> TODO()
            EmptyStructureElement -> TODO()
        }.map { it }
    }

    private val LiteralStructureElement.asNode: Result<String, JsonNode> get() {

        val initialAccumulator: Result<String, LiteralAccumulator> = Success(StartAccumulator)

        return children
                .fold(initialAccumulator) { accumulator, element -> accumulator.flatMap { it.addChar(char = element.value) } }
                .flatMap { if (it is CompleteAccumulator) Success<String, JsonNode>(it.node) else Failure<String, JsonNode>("") }
    }

    interface LiteralAccumulator {

        fun addChar(char: Char): Result<String, LiteralAccumulator>
    }

    object StartAccumulator : LiteralAccumulator {

        override fun addChar(char: Char): Result<String, LiteralAccumulator> {
            return when (char) {
                'n' -> Success(NullAccumulator(previousChar = 'n'))
                't' -> Success(TrueAccumulator(previousChar = 't'))
                'f' -> Success(FalseAccumulator(previousChar = 'f'))
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> Success(IntegerAccumulator(value = char.toString()))
                else -> TODO()
            }
        }
    }

    data class IntegerAccumulator(
            val value: String
    ) : CompleteAccumulator {

        override fun addChar(char: Char): Result<String, LiteralAccumulator> {

            return when (char) {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> Success(copy(value = value + char))
                '.' -> Success(DoublePointAccumulator(value = value + char))
                else -> TODO()
            }
        }

        override val node: JsonNode get() = IntegerNode(value = value.toLong())
    }

    data class DoublePointAccumulator(
            val value: String
    ) : LiteralAccumulator {

        override fun addChar(char: Char): Result<String, LiteralAccumulator> {

            return when (char) {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> Success(DoubleAccumulator(value = value + char))
                else -> TODO()
            }
        }
    }

    data class DoubleAccumulator(
            val value: String
    ) : CompleteAccumulator {

        override fun addChar(char: Char): Result<String, LiteralAccumulator> {

            return when (char) {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> Success(copy(value = value + char))
                else -> TODO()
            }
        }

        override val node: JsonNode get() = DoubleNode(value = value.toDouble())
    }

    class NullAccumulator(
            previousChar: Char
    ) : LiteralWordAccumulator(previousChar = previousChar, word = "null", node = NullNode) {

        override fun copy(previousChar: Char): LiteralWordAccumulator {
            return NullAccumulator(previousChar = previousChar)
        }
    }

    class TrueAccumulator(
            previousChar: Char
    ) : LiteralWordAccumulator(previousChar = previousChar, word = "true", node = TrueNode) {

        override fun copy(previousChar: Char): LiteralWordAccumulator {
            return TrueAccumulator(previousChar = previousChar)
        }
    }

    class FalseAccumulator(
            previousChar: Char
    ) : LiteralWordAccumulator(previousChar = previousChar, word = "false", node = FalseNode) {

        override fun copy(previousChar: Char): LiteralWordAccumulator {
            return FalseAccumulator(previousChar = previousChar)
        }
    }

    abstract class LiteralWordAccumulator(
            val previousChar: Char,
            val word: String,
            val node: JsonNode
    ) : LiteralAccumulator {

        val zippedWord: List<Pair<Char, Char>> = word.dropLast(1).zip(word.drop(1))

        val lastPair: Pair<Char, Char> = zippedWord.last()

        override fun addChar(char: Char): Result<String, LiteralAccumulator> {

            val initial: Result<String, LiteralAccumulator> = Failure("")

            return zippedWord.fold(initial) { accumulator, pair ->

                val (previousCharCandidate, charCandidate) = pair

                accumulator.flatMapError {

                    val matches = previousCharCandidate == previousChar && charCandidate == char

                    if (pair == lastPair && matches) {
                        Success<String, LiteralAccumulator>(LiteralCompleteAccumulator(node = node))
                    } else if (matches) {
                        Success<String, LiteralAccumulator>(copy(previousChar = char))
                    } else {
                        Failure<String, LiteralAccumulator>(it)
                    }
                }
            }
        }

        abstract fun copy(previousChar: Char): LiteralWordAccumulator
    }

    interface CompleteAccumulator : LiteralAccumulator {

        val node: JsonNode
    }

    data class LiteralCompleteAccumulator(
            override val node: JsonNode
    ) : CompleteAccumulator {

        override fun addChar(char: Char): Result<String, LiteralAccumulator> {
            return when (node) {
                is NullNode -> "null"
                is TrueNode -> "true"
                is FalseNode -> "false"
                else -> "?"
            }.plus(char).let { invalidStructure ->
                Failure("Invalid JSON - $invalidStructure")
            }
        }
    }

    private val StringStructureElement.asNode: StringNode get() = StringNode(value = value)

    private val StringStructureElement.value: String
        get() = children.map(StringValue::value).joinToString("")

    private val ArrayStructureElement.asNode: Result<String, ArrayNode>
        get() = children
                .map { translate(it) }
                .traverse()
                .map { ArrayNode(elements = it) }

    private val OpenObjectStructure.asNode: Result<String, ObjectNode>
        get() = children
                .map { it.asObjectChild }
                .traverse()
                .map { ObjectNode(members = it) }

    private val ObjectChildElement<*>.asObjectChild: Result<String, ObjectChild>
        get() = value.asNode.map { ObjectChild(key = key.value, value = it) }
}