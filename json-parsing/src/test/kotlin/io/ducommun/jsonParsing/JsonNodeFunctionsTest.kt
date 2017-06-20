package io.ducommun.jsonParsing

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class JsonNodeFunctionsTest {

    val jackson = ObjectMapper().registerKotlinModule()

    val String.asJsonNode get() = jackson.readValue<com.fasterxml.jackson.databind.JsonNode>(this)

    @Test
    fun `equalsJackson compares NullNode with Jackson nodes`() {

        assertThat(NullNode.equalsJackson("null".asJsonNode)).isTrue()
    }

    @Test
    fun `equalsJackson compares BooleanNode with Jackson nodes`() {

        assertThat(TrueNode.equalsJackson("true".asJsonNode)).isTrue()
        assertThat(FalseNode.equalsJackson("false".asJsonNode)).isTrue()
    }

    @Test
    fun `equalsJackson compares NumberNode with Jackson nodes`() {

        assertThat(IntegerNode(1).equalsJackson("1".asJsonNode)).isTrue()
        assertThat(DoubleNode(1.0).equalsJackson("1.0".asJsonNode)).isTrue()
    }

    @Test
    fun `equalsJackson compares StringNode with Jackson nodes`() {

        assertThat(StringNode("Hello").equalsJackson("\"Hello\"".asJsonNode)).isTrue()
    }

    @Test
    fun `equalsJackson compares ArrayNode with Jackson nodes`() {

        assertThat(ArrayNode(elements = emptyList()).equalsJackson("1".asJsonNode)).isFalse()

        assertThat(ArrayNode(listOf(IntegerNode(1),IntegerNode(2),IntegerNode(3)))
                .equalsJackson("[1,2,3]".asJsonNode)).isTrue()

        assertThat(ArrayNode(listOf(IntegerNode(1),IntegerNode(2),IntegerNode(3)))
                .equalsJackson("[3,2,1]".asJsonNode)).isFalse()

        assertThat(ArrayNode(listOf(IntegerNode(1),IntegerNode(2),IntegerNode(3)))
                .equalsJackson("[1,2]".asJsonNode)).isFalse()

        assertThat(ArrayNode(listOf(IntegerNode(1),IntegerNode(2)))
                .equalsJackson("[1,2,3]".asJsonNode)).isFalse()
    }

    @Test
    fun `equalsJackson compares ObjectNode with Jackson nodes`() {

        assertThat(ObjectNode(members = emptyList()).equalsJackson("1".asJsonNode)).isFalse()

        assertThat(ObjectNode(members = emptyList()).equalsJackson("{\"a\": 1}".asJsonNode)).isFalse()

        assertThat(ObjectNode(members = listOf(
                ObjectMember(key = "A", value = IntegerNode(1)),
                ObjectMember(key = "B", value = IntegerNode(2))
        )).equalsJackson("{\"A\":1,  \"B\":2}".asJsonNode)).isTrue()

        assertThat(ObjectNode(members = listOf(
                ObjectMember(key = "A", value = IntegerNode(1)),
                ObjectMember(key = "B", value = IntegerNode(2))
        )).equalsJackson("{\"A\":1,  \"B\":4}".asJsonNode)).isFalse()

        assertThat(ObjectNode(members = listOf(
                ObjectMember(key = "A", value = IntegerNode(1)),
                ObjectMember(key = "B", value = IntegerNode(2))
        )).equalsJackson("{\"A\": {\"B\": [1, true, \"parsin' the jason\"]}}".asJsonNode)).isFalse()
    }
}