package io.ducommun.jsonParsing

// TODO wouldn't it be cool to override "==" ?
fun JsonNode.equalsJackson(jacksonNode: JacksonJsonNode): Boolean {

    return when (this) {
        NullNode -> jacksonNode.isNull
        is BooleanNode -> jacksonNode.isBoolean && jacksonNode.booleanValue() == value
        is StringNode -> jacksonNode.isTextual && jacksonNode.textValue() == value
        is ArrayNode -> equalsJacksonJsonNode(jacksonNode)
        is DoubleNode -> jacksonNode.isDouble && jacksonNode.doubleValue() == value
        is IntegerNode -> jacksonNode.isInt && jacksonNode.intValue() == value.toInt()
        is ObjectNode -> equalsJacksonJsonNode(jacksonNode)
    }
}

private fun ArrayNode.equalsJacksonJsonNode(jacksonNode: JacksonJsonNode): Boolean =
        jacksonNode.isArray
                && jacksonNode.size() == elements.size
                && elementsEqualsJacksonArrayNodeElements(jacksonNode as JacksonArrayNode)

private fun ArrayNode.elementsEqualsJacksonArrayNodeElements(jacksonNode: JacksonArrayNode): Boolean =
        elements.zip(jacksonNode.elements).fold(true) { acc, (element, jacksonElement) ->
            acc && element.equalsJackson(jacksonElement)
        }

private fun ObjectNode.equalsJacksonJsonNode(jacksonNode: JacksonJsonNode): Boolean =
        jacksonNode.isObject
                && jacksonNode.size() == members.size
                && membersEqualsJacksonObjectNodeMembers(jacksonNode as JacksonObjectNode)

private fun ObjectNode.membersEqualsJacksonObjectNodeMembers(jacksonNode: JacksonObjectNode): Boolean =
        members.fold(true) { acc, (key, value) -> acc && value.equalsJackson(jacksonNode.get(key)) }

private val JacksonArrayNode.elements: List<JacksonJsonNode>
    get() = mutableListOf<JacksonJsonNode>().apply { elements().forEachRemaining { add(it) } }.toList()
