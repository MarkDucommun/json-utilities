package com.hcsc.de.claims.jsonParsingFour

import com.hcsc.de.claims.failsWithMessage
import com.hcsc.de.claims.succeedsAndShouldReturn
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
            ObjectChild(key = "a", value = StringNode(value = "1"))
        ))
    }

    @Test
    fun `it translates a null`() {

        LiteralStructureElement(
                id = 1,
                children = listOf(
                        LiteralValue(id = 1, value = 'n'),
                        LiteralValue(id = 1, value = 'u'),
                        LiteralValue(id = 1, value = 'l'),
                        LiteralValue(id = 1, value = 'l')
                )
        ).asNode succeedsAndShouldReturn NullNode
    }

    @Test
    fun `it translates a true`() {

        LiteralStructureElement(
                id = 1,
                children = listOf(
                        LiteralValue(id = 1, value = 't'),
                        LiteralValue(id = 1, value = 'r'),
                        LiteralValue(id = 1, value = 'u'),
                        LiteralValue(id = 1, value = 'e')
                )
        ).asNode succeedsAndShouldReturn TrueNode
    }

    @Test
    fun `it translates a false`() {

        LiteralStructureElement(
                id = 1,
                children = listOf(
                        LiteralValue(id = 1, value = 'f'),
                        LiteralValue(id = 1, value = 'a'),
                        LiteralValue(id = 1, value = 'l'),
                        LiteralValue(id = 1, value = 's'),
                        LiteralValue(id = 1, value = 'e')
                )
        ).asNode succeedsAndShouldReturn FalseNode
    }

    @Test
    fun `it fails when proceeding past a successful null`() {

        LiteralStructureElement(
                id = 1,
                children = listOf(
                        LiteralValue(id = 1, value = 'n'),
                        LiteralValue(id = 1, value = 'u'),
                        LiteralValue(id = 1, value = 'l'),
                        LiteralValue(id = 1, value = 'l'),
                        LiteralValue(id = 1, value = 'l')
                )
        ).asNode failsWithMessage "Invalid JSON - nulll"
    }

    @Test
    fun `it fails when proceeding past a successful true`() {

        LiteralStructureElement(
                id = 1,
                children = listOf(
                        LiteralValue(id = 1, value = 't'),
                        LiteralValue(id = 1, value = 'r'),
                        LiteralValue(id = 1, value = 'u'),
                        LiteralValue(id = 1, value = 'e'),
                        LiteralValue(id = 1, value = 'e')
                )
        ).asNode failsWithMessage "Invalid JSON - truee"
    }

    @Test
    fun `it fails when proceeding past a successful literal`() {

        LiteralStructureElement(
                id = 1,
                children = listOf(
                        LiteralValue(id = 1, value = 'f'),
                        LiteralValue(id = 1, value = 'a'),
                        LiteralValue(id = 1, value = 'l'),
                        LiteralValue(id = 1, value = 's'),
                        LiteralValue(id = 1, value = 'e'),
                        LiteralValue(id = 1, value = 'f')
                )
        ).asNode failsWithMessage "Invalid JSON - falsef"
    }

    @Test
    fun `it parses a simple integer number`() {

        '0'.rangeTo('9').forEach {

            LiteralStructureElement(
                    id = 1,
                    children = listOf(
                            LiteralValue(id = 1, value = it)
                    )
            ).asNode succeedsAndShouldReturn IntegerNode(value = it.toString().toLong())
        }
    }

    @Test
    fun `it parses a slightly longer integer number`() {

        '0'.rangeTo('9').forEach {

            LiteralStructureElement(
                    id = 1,
                    children = listOf(
                            LiteralValue(id = 1, value = '1'),
                            LiteralValue(id = 1, value = it)
                    )
            ).asNode succeedsAndShouldReturn IntegerNode(value = it.toString().toLong() + 10)
        }
    }

    @Test
    fun `it parses a simple double number`() {

        '0'.rangeTo('9').forEach {

            LiteralStructureElement(
                    id = 1,
                    children = listOf(
                            LiteralValue(id = 1, value = it),
                            LiteralValue(id = 1, value = '.'),
                            LiteralValue(id = 1, value = it)
                    )
            ).asNode succeedsAndShouldReturn DoubleNode(value = "$it.$it".toDouble())
        }
    }

    @Test
    fun `it parses a longer double number`() {

        '0'.rangeTo('9').forEach {

            LiteralStructureElement(
                    id = 1,
                    children = listOf(
                            LiteralValue(id = 1, value = '0'),
                            LiteralValue(id = 1, value = '.'),
                            LiteralValue(id = 1, value = '0'),
                            LiteralValue(id = 1, value = it)
                    )
            ).asNode succeedsAndShouldReturn DoubleNode(value = "0.0$it".toDouble())
        }
    }

    private val subject = JsonStructureTranslator()

    private val MainStructure<*>.asNode get() = subject.translate(this)
}