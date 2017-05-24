package com.hcsc.de.claims.jsonParsingFour

import com.hcsc.de.claims.failsWithMessage
import com.hcsc.de.claims.succeedsAndShouldReturn
import org.junit.Test

class JsonStructureParserArrayTest : JsonStructureParserBaseTest() {

    @Test
    fun `it can create an empty array`() {

        "[]".jsonStructure succeedsAndShouldReturn listOf(ArrayOpen(id = 1), ArrayClose(id = 1))
    }

    @Test
    fun `it handles whitespace around arrays`() {

        forEachWhitespaceChar { char ->

            "$char[$char]$char".jsonStructure succeedsAndShouldReturn listOf(
                    ArrayOpen(id = 1),
                    ArrayClose(id = 1)
            )
        }
    }

    @Test
    fun `it can create an array with a single literal child`() {

        "[a]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                LiteralClose(id = 2, value = 'a'),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create an array with a single literal child with whitespace`() {

        forEachWhitespaceChar { char ->

            "[a$char]".jsonStructure succeedsAndShouldReturn listOf(
                    ArrayOpen(id = 1),
                    LiteralClose(id = 2, value = 'a'),
                    ArrayClose(id = 1)
            )
        }
    }

    @Test
    fun `it can create an array with a single literal child with more than one whitespace`() {

        forEachWhitespaceChar { char ->

            "[a$char$char]".jsonStructure succeedsAndShouldReturn listOf(
                    ArrayOpen(id = 1),
                    LiteralClose(id = 2, value = 'a'),
                    ArrayClose(id = 1)
            )
        }
    }

    @Test
    fun `it can create an array with a two literal children`() {

        "[a,b]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                LiteralClose(id = 2, value = 'a'),
                ArrayComma(id = 1),
                LiteralClose(id = 3, value = 'b'),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create an array with a two literal children and some whitespace`() {

        forEachWhitespaceChar { char ->

            "[a$char,${char}b]".jsonStructure succeedsAndShouldReturn listOf(
                    ArrayOpen(id = 1),
                    LiteralClose(id = 2, value = 'a'),
                    ArrayComma(id = 1),
                    LiteralClose(id = 3, value = 'b'),
                    ArrayClose(id = 1)
            )
        }
    }

    @Test
    fun `it can create an array with an empty string child`() {

        "[\"\"]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                StringOpen(id = 2),
                StringClose(id = 2),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create an array with a string child`() {

        "[\"a\"]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                StringOpen(id = 2),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 2),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create an array with two string children `() {

        "[\"\",\"b\"]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                StringOpen(id = 2),
                StringClose(id = 2),
                ArrayComma(id = 1),
                StringOpen(id = 3),
                StringValue(id = 3, value = 'b'),
                StringClose(id = 3),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create an array with a string child and a literal child `() {

        "[a,\"\"]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                LiteralClose(id = 2, value = 'a'),
                ArrayComma(id = 1),
                StringOpen(id = 3),
                StringClose(id = 3),
                ArrayClose(id = 1)
        )

        "[\"\",a]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                StringOpen(id = 2),
                StringClose(id = 2),
                ArrayComma(id = 1),
                LiteralClose(id = 3, value = 'a'),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create an array with an empty array child`() {

        "[[]]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                ArrayOpen(id = 2),
                ArrayClose(id = 2),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create an array with an empty object child`() {

        "[{}]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                ObjectOpen(id = 2),
                ObjectClose(id = 2),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create an array with an object child with a literal child`() {

        "[{\"a\":a}]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                ObjectOpen(id = 2),
                StringOpen(id = 3),
                StringValue(id = 3, value = 'a'),
                StringClose(id = 3),
                ObjectColon(id = 2),
                LiteralClose(id = 4, value = 'a'),
                ObjectClose(id = 2),
                ArrayClose(id = 1)
        )
    }


    @Test
    fun `it can create an array with an object child with a string child`() {

        "[{\"\":\"\"}]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                ObjectOpen(id = 2),
                StringOpen(id = 3),
                StringClose(id = 3),
                ObjectColon(id = 2),
                StringOpen(id = 4),
                StringClose(id = 4),
                ObjectClose(id = 2),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create an array with an object child with an array child`() {

        "[{\"\":[]}]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                ObjectOpen(id = 2),
                StringOpen(id = 3),
                StringClose(id = 3),
                ObjectColon(id = 2),
                ArrayOpen(id = 4),
                ArrayClose(id = 4),
                ObjectClose(id = 2),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it fails when anything other than a curly brace closes an object`() {

        "[{\"\":[]]".jsonStructure failsWithMessage "Invalid JSON - must close an object with a curly brace"
    }

    @Test
    fun `it can create an array with an object child with an object child`() {

        "[{\"\":{}}]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                ObjectOpen(id = 2),
                StringOpen(id = 3),
                StringClose(id = 3),
                ObjectColon(id = 2),
                ObjectOpen(id = 4),
                ObjectClose(id = 4),
                ObjectClose(id = 2),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create an array with an object child with a literal child with whitespace`() {

        forEachWhitespaceChar { char ->

            "[{\"a\":${char}a$char}]".jsonStructure succeedsAndShouldReturn listOf(
                    ArrayOpen(id = 1),
                    ObjectOpen(id = 2),
                    StringOpen(id = 3),
                    StringValue(id = 3, value = 'a'),
                    StringClose(id = 3),
                    ObjectColon(id = 2),
                    LiteralClose(id = 4, value = 'a'),
                    ObjectClose(id = 2),
                    ArrayClose(id = 1)
            )
        }
    }

    @Test
    fun `it can create an array with an empty object child and whitespace`() {

        forEachWhitespaceChar { char ->

            "[$char{$char}$char]".jsonStructure succeedsAndShouldReturn listOf(
                    ArrayOpen(id = 1),
                    ObjectOpen(id = 2),
                    ObjectClose(id = 2),
                    ArrayClose(id = 1)
            )
        }
    }


    @Test
    fun `it can create an array with an array child with an empty object child`() {

        "[[{}]]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                ArrayOpen(id = 2),
                ObjectOpen(id = 3),
                ObjectClose(id = 3),
                ArrayClose(id = 2),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create an array with an array child with an emtpy array child`() {

        "[[[]]]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                ArrayOpen(id = 2),
                ArrayOpen(id = 3),
                ArrayClose(id = 3),
                ArrayClose(id = 2),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create an array with an array child with an array child an empty array child`() {

        "[[[[]]]]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                ArrayOpen(id = 2),
                ArrayOpen(id = 3),
                ArrayOpen(id = 4),
                ArrayClose(id = 4),
                ArrayClose(id = 3),
                ArrayClose(id = 2),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create an array with an array child with a literal`() {

        "[[a]]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                ArrayOpen(id = 2),
                LiteralClose(id = 3, value = 'a'),
                ArrayClose(id = 2),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create an array with an array child with a longer literal child`() {

        "[[ab]]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                ArrayOpen(id = 2),
                LiteralValue(id = 3, value = 'a'),
                LiteralClose(id = 3, value = 'b'),
                ArrayClose(id = 2),
                ArrayClose(id = 1)
        )
    }


    @Test
    fun `it can create an array with multiple children including an array with a longer literal child`() {

        "[[ab], a]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                ArrayOpen(id = 2),
                LiteralValue(id = 3, value = 'a'),
                LiteralClose(id = 3, value = 'b'),
                ArrayClose(id = 2),
                ArrayComma(id = 1),
                LiteralClose(id = 4, value = 'a'),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create a triply nest array with a literal`() {

        "[[[ab]]]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                ArrayOpen(id = 2),
                ArrayOpen(id = 3),
                LiteralValue(id = 4, value = 'a'),
                LiteralClose(id = 4, value = 'b'),
                ArrayClose(id = 3),
                ArrayClose(id = 2),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create a triply nest array with a literal and whitespace`() {

        forEachWhitespaceChar { char ->

            "[$char[$char[${char}ab$char]$char]$char]".jsonStructure succeedsAndShouldReturn listOf(
                    ArrayOpen(id = 1),
                    ArrayOpen(id = 2),
                    ArrayOpen(id = 3),
                    LiteralValue(id = 4, value = 'a'),
                    LiteralClose(id = 4, value = 'b'),
                    ArrayClose(id = 3),
                    ArrayClose(id = 2),
                    ArrayClose(id = 1)
            )
        }
    }

    @Test
    fun `it can create a doubly nested array with a string`() {

        "[[[\"ab\"]]]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                ArrayOpen(id = 2),
                ArrayOpen(id = 3),
                StringOpen(id = 4),
                StringValue(id = 4, value = 'a'),
                StringValue(id = 4, value = 'b'),
                StringClose(id = 4),
                ArrayClose(id = 3),
                ArrayClose(id = 2),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create a doubly nested array with multiple string children`() {

        "[[[\"ab\", \"ab\"]]]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                ArrayOpen(id = 2),
                ArrayOpen(id = 3),
                StringOpen(id = 4),
                StringValue(id = 4, value = 'a'),
                StringValue(id = 4, value = 'b'),
                StringClose(id = 4),
                ArrayComma(id = 3),
                StringOpen(id = 5),
                StringValue(id = 5, value = 'a'),
                StringValue(id = 5, value = 'b'),
                StringClose(id = 5),
                ArrayClose(id = 3),
                ArrayClose(id = 2),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create a doubly nested array with multiple empty array children`() {

        "[[[], []], [[], []]]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                ArrayOpen(id = 2),
                ArrayOpen(id = 3),
                ArrayClose(id = 3),
                ArrayComma(id = 2),
                ArrayOpen(id = 4),
                ArrayClose(id = 4),
                ArrayClose(id = 2),
                ArrayComma(id = 1),
                ArrayOpen(id = 5),
                ArrayOpen(id = 6),
                ArrayClose(id = 6),
                ArrayComma(id = 5),
                ArrayOpen(id = 7),
                ArrayClose(id = 7),
                ArrayClose(id = 5),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create an array where the second child is an empty object`() {

        "[a,{}]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                LiteralClose(id = 2, value = 'a'),
                ArrayComma(id = 1),
                ObjectOpen(id = 3),
                ObjectClose(id = 3),
                ArrayClose(id = 1)
        )
    }

    @Test
    fun `it can create an array where the children are is empty objects`() {

        "[{},{}]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                ObjectOpen(id = 2),
                ObjectClose(id = 2),
                ArrayComma(id = 1),
                ObjectOpen(id = 3),
                ObjectClose(id = 3),
                ArrayClose(id = 1)
        )
    }
}