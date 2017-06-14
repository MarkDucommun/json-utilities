package io.ducommun.jsonParsing

import com.hcsc.de.claims.results.failsWithMessage
import com.hcsc.de.claims.results.succeedsAndShouldReturn
import org.junit.Test

class JsonStructureTranslatorTest {

    @Test
    fun `it translates a simple string structure`() {

        StringStructureElement(id = 1, children = listOf(
                StringValue(id = 1, value = 'a')
        )).asNode succeedsAndShouldReturn StringNode(value = "a")
    }

    @Test
    fun `it translates an empty array`() {

        ArrayStructureElement(id = 1, children = emptyList())
                .asNode succeedsAndShouldReturn ArrayNode(elements = emptyList())
    }

    @Test
    fun `it translates an array with a string child`() {

        ArrayStructureElement(id = 1, children = listOf(
                StringStructureElement(id = 1, children = listOf(StringValue(id = 1, value = 'a')))
        )).asNode succeedsAndShouldReturn ArrayNode(elements = listOf(StringNode(value = "a")))
    }

    @Test
    fun `it translates an array with several string children`() {

        ArrayStructureElement(id = 1, children = listOf(
                StringStructureElement(id = 2, children = listOf(StringValue(id = 1, value = 'a'))),
                StringStructureElement(id = 3, children = listOf(StringValue(id = 1, value = 'b'))),
                StringStructureElement(id = 4, children = listOf(StringValue(id = 1, value = 'c')))
        )).asNode succeedsAndShouldReturn ArrayNode(elements = listOf(
                StringNode(value = "a"),
                StringNode(value = "b"),
                StringNode(value = "c")
        ))
    }

    @Test
    fun `it translates an object with children`() {

        OpenObjectStructure(id = 1, children = listOf(
                ObjectChildElement(
                        id = 3,
                        key = StringStructureElement(id = 2, children = listOf(
                                StringValue(id = 2, value = 'a')
                        )),
                        value = StringStructureElement(id = 2, children = listOf(
                                StringValue(id = 3, value = '1')
                        ))
                )
        )).asNode succeedsAndShouldReturn ObjectNode(members = listOf(
            ObjectMember(key = "a", value = StringNode(value = "1"))
        ))
    }

    @Test
    fun `it translates a null`() {

        literalStructure("null").asNode succeedsAndShouldReturn NullNode
    }

    @Test
    fun `it translates a true`() {

        literalStructure("true").asNode succeedsAndShouldReturn TrueNode
    }

    @Test
    fun `it translates a false`() {

        literalStructure("false").asNode succeedsAndShouldReturn FalseNode
    }

    @Test
    fun `it fails when proceeding past a successful null`() {

        literalStructure("nulll").asNode failsWithMessage "Invalid JSON - nulll"
    }

    @Test
    fun `it fails when proceeding past a successful true`() {

        literalStructure("truee").asNode failsWithMessage "Invalid JSON - truee"
    }

    @Test
    fun `it fails when proceeding past a successful literal`() {

        literalStructure("falsef").asNode failsWithMessage "Invalid JSON - falsef"
    }

    @Test
    fun `it fails to parse invalid characters following valid literal beginnings`() {

        literalStructure("falsa").asNode failsWithMessage "Invalid JSON - incorrect literal"
    }

    @Test
    fun `it fails to parse incomplete literals`() {

        literalStructure("fals").asNode failsWithMessage "Invalid JSON - incomplete literal"
    }

    @Test
    fun `it parses a simple integer number`() {

        forAllDigits {

            literalStructure("$it").asNode succeedsAndShouldReturn IntegerNode(value = it.toString().toLong())
        }
    }

    @Test
    fun `it parses a slightly longer integer number`() {

        forAllDigits {

            literalStructure("1$it").asNode succeedsAndShouldReturn IntegerNode(value = it.toString().toLong() + 10)
        }
    }

    @Test
    fun `it parses a simple double number`() {

        forAllDigits {

            literalStructure("$it.$it").asNode succeedsAndShouldReturn DoubleNode(value = "$it.$it".toDouble())
        }
    }

    @Test
    fun `it parses a longer double number`() {

        forAllDigits {

            literalStructure("0.0$it").asNode succeedsAndShouldReturn DoubleNode(value = "0.0$it".toDouble())
        }
    }

    @Test
    fun `it fails to parse an unfinished double number`() {

            literalStructure("0.").asNode failsWithMessage "Invalid JSON - incomplete literal"
    }

    @Test
    fun `it fails to an improperly started double`() {

        literalStructure(".0").asNode failsWithMessage "Invalid JSON - float literals must start with a digit"
    }

    @Test
    fun `it parses negative integers`() {

        literalStructure("-1").asNode succeedsAndShouldReturn IntegerNode(value = -1)
    }

    @Test
    fun `it parses negative integer zero`() {

        literalStructure("-0").asNode succeedsAndShouldReturn IntegerNode(value = -0)
    }

    @Test
    fun `it parses negative double zero`() {

        literalStructure("-0.0").asNode succeedsAndShouldReturn DoubleNode(value = -0.0)
    }

    @Test
    fun `it parses negative doubles starting with zero`() {

        literalStructure("-0.1").asNode succeedsAndShouldReturn DoubleNode(value = -0.1)
    }

    @Test
    fun `it parses simple exponents as doubles`() {

        literalStructure("1E1").asNode succeedsAndShouldReturn DoubleNode(value = 1E1)
        literalStructure("1e1").asNode succeedsAndShouldReturn DoubleNode(value = 1E1)
        literalStructure("0e1").asNode succeedsAndShouldReturn DoubleNode(value = 0E1)
    }

    @Test
    fun `it parses positive exponents as doubles`() {

        literalStructure("1E+1").asNode succeedsAndShouldReturn DoubleNode(value = 1E+1)
        literalStructure("1e+1").asNode succeedsAndShouldReturn DoubleNode(value = 1E+1)
        literalStructure("0e+1").asNode succeedsAndShouldReturn DoubleNode(value = 0E+1)
    }

    @Test
    fun `it parses negative exponents as doubles`() {

        literalStructure("1E-1").asNode succeedsAndShouldReturn DoubleNode(value = 1E-1)
        literalStructure("1e-1").asNode succeedsAndShouldReturn DoubleNode(value = 1E-1)
        literalStructure("0e-1").asNode succeedsAndShouldReturn DoubleNode(value = 0E-1)
    }

    @Test
    fun `it parses extreme exponents`() {

        literalStructure("1.0E-1").asNode succeedsAndShouldReturn DoubleNode(value = 1.0E-1)
        literalStructure("1.0e-1").asNode succeedsAndShouldReturn DoubleNode(value = 1.0E-1)
        literalStructure("0.0e-1").asNode succeedsAndShouldReturn DoubleNode(value = 0.0E-1)
    }

    @Test
    fun `it fails to parse leading zeros`() {

        literalStructure("01.1").asNode failsWithMessage "Invalid JSON - leading zeros are not permitted"
    }

    @Test
    fun `it fails to parse negative leading zeros`() {

        literalStructure("-01.1").asNode failsWithMessage "Invalid JSON - leading zeros are not permitted"
    }

    @Test
    fun `it fails to parse invalid characters after a decimal point`() {

        literalStructure("1.A").asNode failsWithMessage "Invalid JSON - 'A' may not be part of a number"
    }

    @Test
    fun `it fails to parse invalid characters in a double`() {

        literalStructure("1.0A").asNode failsWithMessage "Invalid JSON - 'A' may not be part of a number"
    }

    @Test
    fun `it fails to parse invalid characters in an integer`() {

        literalStructure("1A").asNode failsWithMessage "Invalid JSON - 'A' may not be part of a number"
    }

    @Test
    fun `it fails to parse invalid characters after a negative`() {

        literalStructure("-A").asNode failsWithMessage "Invalid JSON - 'A' may not be part of a number"
    }

    @Test
    fun `it fails to parse invalid characters starting a literal`() {

        literalStructure("A").asNode failsWithMessage "Invalid JSON - 'A' is not a valid literal"
    }

    private val subject = JsonStructureTranslator()

    private val MainStructure<*>.asNode get() = subject.translate(this)

    private fun literalStructure(value: String): LiteralStructureElement =
            LiteralStructureElement(id = 1, children = value.toCharArray().map { LiteralValue(id = 1, value = it) })

    private fun forAllDigits(fn: (Char) -> Unit) = '0'.rangeTo('9').forEach(fn)
}