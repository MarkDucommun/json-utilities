package io.ducommun.jsonParsing

sealed class JsonNode

object NullNode : JsonNode()

abstract class BooleanNode : JsonNode() { abstract val value: Boolean }

object TrueNode : BooleanNode() { override val value = true }

object FalseNode : BooleanNode() { override val value = false }

sealed class NumberNode : JsonNode()

data class IntegerNode(val value: Long) : NumberNode()

data class DoubleNode(val value: Double) : NumberNode()

data class StringNode(val value: String) : JsonNode()

data class ObjectNode(val members: List<ObjectMember>) : JsonNode()

data class ObjectMember(val key: String, val value: JsonNode)

data class ArrayNode(val elements: List<JsonNode>) : JsonNode()