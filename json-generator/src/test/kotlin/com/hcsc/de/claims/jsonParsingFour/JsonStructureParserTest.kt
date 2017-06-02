package com.hcsc.de.claims.jsonParsingFour

import com.hcsc.de.claims.failsWithMessage
import com.hcsc.de.claims.succeedsAndShouldReturn
import org.junit.Test

class JsonStructureParserTest {

    private val subject = JsonStructureParser()

    val String.jsonStructure get() = subject.parse(this)

    @Test
    fun `simple literal element`() {
        "a".jsonStructure succeedsAndShouldReturn listOf(LiteralValue(id = 1, value = 'a'))
    }

    @Test
    fun `simple literal element with two chars`() {
        "ab".jsonStructure succeedsAndShouldReturn listOf(
                LiteralValue(id = 1, value = 'a'),
                LiteralValue(id = 1, value = 'b')
        )
    }

    @Test
    fun `simple literal element with whitespace`() {

        listOf(' ', '\n', '\r', '\t').forEach { whitespaceChar ->

            "a$whitespaceChar".jsonStructure succeedsAndShouldReturn listOf(LiteralClose(id = 1, value = 'a'))

            "${whitespaceChar}a".jsonStructure succeedsAndShouldReturn listOf(LiteralValue(id = 1, value = 'a'))
        }
    }

    @Test
    fun `it fails when there is a literal following a closed literal`() {
        "a a".jsonStructure failsWithMessage "Invalid JSON - nothing can follow a closed root literal"
    }

    @Test
    fun `it can parse an empty string`() {
        "\"\"".jsonStructure succeedsAndShouldReturn listOf(
                StringOpen(id = 1),
                StringClose(id = 1)
        )
    }

    @Test
    fun `it can parse a simple string with one letter`() {
        "\"a\"".jsonStructure succeedsAndShouldReturn listOf(
                StringOpen(id = 1),
                StringValue(id = 1, value = 'a'),
                StringClose(id = 1)
        )
    }

    @Test
    fun `it can parse a simple string with more than one letter`() {
        "\"ab\"".jsonStructure succeedsAndShouldReturn listOf(
                StringOpen(id = 1),
                StringValue(id = 1, value = 'a'),
                StringValue(id = 1, value = 'b'),
                StringClose(id = 1)
        )
    }

    @Test
    fun `it can parse a simple strings with whitespace`() {

            listOf(' ', '\n', '\r', '\t').forEach { whitespaceChar ->

                "\"a\"$whitespaceChar".jsonStructure succeedsAndShouldReturn listOf(
                        StringOpen(id = 1),
                        StringValue(id = 1, value = 'a'),
                        StringClose(id = 1)
                )

                "$whitespaceChar\"a\"".jsonStructure succeedsAndShouldReturn listOf(
                        StringOpen(id = 1),
                        StringValue(id = 1, value = 'a'),
                        StringClose(id = 1)
                )
            }
    }

    @Test
    fun `it fails when anything other than whitespace follows a top level closed string`() {
        "\"a\" a".jsonStructure failsWithMessage "Invalid JSON - nothing can follow a closed root string"
    }


    @Test
    fun `it can escape quotes and slashes`() {

        listOf('\\', '"', '/').forEach { escapableChar ->
            "\"\\$escapableChar\"".jsonStructure succeedsAndShouldReturn listOf(
                    StringOpen(id = 1),
                    StringValue(id = 1, value = escapableChar),
                    StringClose(id = 1)
            )

            "\"a\\$escapableChar\"".jsonStructure succeedsAndShouldReturn listOf(
                    StringOpen(id = 1),
                    StringValue(id = 1, value = 'a'),
                    StringValue(id = 1, value = escapableChar),
                    StringClose(id = 1)
            )
        }
    }

    @Test
    fun `it fails if an escape is not followed by `() {
        "\"\\a\"".jsonStructure failsWithMessage "Invalid JSON - only quotes and slashes may follow escape characters"
    }

    @Test
    fun `it fails if a string is not closed with quotes`() {
        "\"a".jsonStructure failsWithMessage "Invalid JSON - must close all open elements"
    }

    @Test
    fun `it can create an empty array`() {
        "[]".jsonStructure succeedsAndShouldReturn listOf(ArrayOpen(id = 1), ArrayClose(id = 1))
    }

    @Test
    fun `it handles whitespace around arrays`() {

        listOf(' ', '\n', '\r', '\t').forEach { whitespaceChar ->

            "$whitespaceChar[]".jsonStructure succeedsAndShouldReturn listOf(
                    ArrayOpen(id = 1),
                    ArrayClose(id = 1)
            )

            "[]$whitespaceChar".jsonStructure succeedsAndShouldReturn listOf(
                    ArrayOpen(id = 1),
                    ArrayClose(id = 1)
            )

            "[$whitespaceChar]".jsonStructure succeedsAndShouldReturn listOf(
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

        listOf(' ', '\n', '\r', '\t').forEach { whitespaceChar ->

            "[a$whitespaceChar]".jsonStructure succeedsAndShouldReturn listOf(
                    ArrayOpen(id = 1),
                    LiteralClose(id = 2, value = 'a'),
                    ArrayClose(id = 1)
            )
        }
    }

    @Test
    fun `it can create an array with a single literal child with more than one whitespace`() {

        listOf(' ', '\n', '\r', '\t').forEach { whitespaceChar ->

            "[a$whitespaceChar$whitespaceChar]".jsonStructure succeedsAndShouldReturn listOf(
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

        "[a, b]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                LiteralClose(id = 2, value = 'a'),
                ArrayComma(id = 1),
                LiteralClose(id = 3, value = 'b'),
                ArrayClose(id = 1)
        )

        "[a ,ba]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                LiteralClose(id = 2, value = 'a'),
                ArrayComma(id = 1),
                LiteralValue(id = 3, value = 'b'),
                LiteralClose(id = 3, value = 'a'),
                ArrayClose(id = 1)
        )
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

        "[\"\", \"b\"]".jsonStructure succeedsAndShouldReturn listOf(
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

        "[a, \"\"]".jsonStructure succeedsAndShouldReturn listOf(
                ArrayOpen(id = 1),
                LiteralClose(id = 2, value = 'a'),
                ArrayComma(id = 1),
                StringOpen(id = 3),
                StringClose(id = 3),
                ArrayClose(id = 1)
        )


        "[\"\", a]".jsonStructure succeedsAndShouldReturn listOf(
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
}