package com.hcsc.de.claims.jsonParsingFour

import com.hcsc.de.claims.failsWithMessage
import com.hcsc.de.claims.succeedsAndShouldReturn
import org.junit.Assert.*
import org.junit.Test

class JsonStructureParserTest {

    private val subject = JsonStructureParser()

    val String.jsonStructure get() = subject.parse(this)

    @Test
    fun `simple literal element`() {
        "a".jsonStructure succeedsAndShouldReturn listOf(LiteralChildStructureElement(id = 1, value = 'a'))
    }

    @Test
    fun `simple literal element with whitespace`() {

        listOf(' ', '\n', '\r', '\t').forEach { whitespaceChar ->

            "a$whitespaceChar".jsonStructure succeedsAndShouldReturn listOf(LiteralChildCloseElement(id = 1, value = 'a'))

            "${whitespaceChar}a".jsonStructure succeedsAndShouldReturn listOf(LiteralChildStructureElement(id = 1, value = 'a'))
        }
    }

    @Test
    fun `it fails when there is a literal following a closed literal`() {
        "a a".jsonStructure failsWithMessage "Invalid JSON - nothing can follow a closed root literal"
    }

    @Test
    fun `it can parse an empty string`() {
        "\"\"".jsonStructure succeedsAndShouldReturn listOf(
                StringChildOpenElement(id = 1),
                StringChildCloseElement(id = 1)
        )
    }

    @Test
    fun `it can parse a simple string with one letter`() {
        "\"a\"".jsonStructure succeedsAndShouldReturn listOf(
                StringChildOpenElement(id = 1),
                StringChildStructureElement(id = 1, value = 'a'),
                StringChildCloseElement(id = 1)
        )
    }

    @Test
    fun `it can parse a simple string with more than one letter`() {
        "\"ab\"".jsonStructure succeedsAndShouldReturn listOf(
                StringChildOpenElement(id = 1),
                StringChildStructureElement(id = 1, value = 'a'),
                StringChildStructureElement(id = 1, value = 'b'),
                StringChildCloseElement(id = 1)
        )
    }

    @Test
    fun `it can parse a simple strings with whitespace`() {

            listOf(' ', '\n', '\r', '\t').forEach { whitespaceChar ->

                "\"a\"$whitespaceChar".jsonStructure succeedsAndShouldReturn listOf(
                        StringChildOpenElement(id = 1),
                        StringChildStructureElement(id = 1, value = 'a'),
                        StringChildCloseElement(id = 1)
                )

                "$whitespaceChar\"a\"".jsonStructure succeedsAndShouldReturn listOf(
                        StringChildOpenElement(id = 1),
                        StringChildStructureElement(id = 1, value = 'a'),
                        StringChildCloseElement(id = 1)
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
                    StringChildOpenElement(id = 1),
                    StringChildStructureElement(id = 1, value = escapableChar),
                    StringChildCloseElement(id = 1)
            )

            "\"a\\$escapableChar\"".jsonStructure succeedsAndShouldReturn listOf(
                    StringChildOpenElement(id = 1),
                    StringChildStructureElement(id = 1, value = 'a'),
                    StringChildStructureElement(id = 1, value = escapableChar),
                    StringChildCloseElement(id = 1)
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
}