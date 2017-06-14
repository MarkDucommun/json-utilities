package io.ducommun.jsonParsing

import com.hcsc.de.claims.results.*
import io.ducommun.jsonParsing.literalAccumulators.Accumulator
import io.ducommun.jsonParsing.literalAccumulators.CompleteAccumulator
import io.ducommun.jsonParsing.literalAccumulators.StartAccumulator

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

        val initialAccumulator: Result<String, Accumulator> = Success(StartAccumulator)

        return children
                .fold(initialAccumulator) { result, element -> result.flatMap { it.addChar(char = element.value) } }
                .flatMap { it.isComplete }
    }

    private val Accumulator.isComplete: Result<String, JsonNode> get() {

        return if (this is CompleteAccumulator) node else Failure<String, JsonNode>("Invalid JSON - incomplete literal")
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

    private val ObjectChildElement<*>.asObjectChild: Result<String, ObjectMember>
        get() = value.asNode.map { ObjectMember(key = key.value, value = it) }
}