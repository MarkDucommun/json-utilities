package com.hcsc.de.claims.jsonParsingFour

import com.hcsc.de.claims.failsWithMessage
import com.hcsc.de.claims.succeedsAndShouldReturn
import org.junit.Test

class JsonStructureParserStringTest : JsonStructureParserBaseTest() {

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

        forEachWhitespaceChar { char ->

            "$char\"a\"$char".jsonStructure succeedsAndShouldReturn listOf(
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
}