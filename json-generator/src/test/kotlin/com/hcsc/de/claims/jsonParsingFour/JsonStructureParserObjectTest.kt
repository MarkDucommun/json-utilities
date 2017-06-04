package com.hcsc.de.claims.jsonParsingFour

import com.hcsc.de.claims.failsWithMessage
import com.hcsc.de.claims.succeedsAndShouldReturn
import org.junit.Test

class JsonStructureParserObjectTest : JsonStructureParserBaseTest() {

    @Test
    fun `it can create an empty object`() {

        "{}".jsonStructure succeedsAndShouldReturn listOf(
                ObjectOpen(id = 1),
                ObjectClose(id = 1)
        )
    }

    @Test
    fun `it can create an empty object with whitespace`() {

        forEachWhitespaceChar { char ->

            "$char{$char}$char".jsonStructure succeedsAndShouldReturn listOf(
                    ObjectOpen(id = 1),
                    ObjectClose(id = 1)
            )
        }
    }

    @Test
    fun `it fails when the empty object is not closed`() {

        "{".jsonStructure failsWithMessage "Invalid JSON - must close all open elements"
    }

    @Test
    fun `it fails when there is anything following a closed root object`() {

        "{}a".jsonStructure failsWithMessage "Invalid JSON - nothing can follow a closed root object"
    }

    @Test
    fun `it fails when there is anything other than a string following a object open`() {

        "{a".jsonStructure failsWithMessage "Invalid JSON - object key must be a string"
    }

    @Test
    fun `it fails when there is anything other than a colon following an object key`() {

        "{\"a\"a".jsonStructure failsWithMessage "Invalid JSON - colon must follow an object key"
    }


    @Test
    fun `it fails when the object is closed after a key but before a value`() {

        "{\"a\"}".jsonStructure failsWithMessage "Invalid JSON - colon must follow an object key"
    }

    @Test
    fun `it fails when the simple object is not closed`() {

        "{\"a\":a".jsonStructure failsWithMessage "Invalid JSON - must close all open elements"
    }

    @Test
    fun `it fails when the object is closed after a colon but before a value`() {

        "{\"a\":}".jsonStructure failsWithMessage "Invalid JSON - a value must follow a colon"
    }

    @Test
    fun `it creates a simple object with a literal value`() {

        "{\"a\":a}".jsonStructure succeedsAndShouldReturn listOf(
                ObjectOpen(id = 1),
                StringOpen(id = 2),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 2),
                ObjectColon(id = 1),
                LiteralClose(id = 3, value = 'a'),
                ObjectClose(id = 1)
        )
    }

    @Test
    fun `it can create an object with a literal child and whitespace`() {

        forEachWhitespaceChar { char ->

            "{$char\"a\"$char:${char}a$char}".jsonStructure succeedsAndShouldReturn listOf(
                    ObjectOpen(id = 1),
                    StringOpen(id = 2),
                    StringValue(id = 2, value = 'a'),
                    StringClose(id = 2),
                    ObjectColon(id = 1),
                    LiteralClose(id = 3, value = 'a'),
                    ObjectClose(id = 1)
            )
        }
    }

    @Test
    fun `it creates a simple object with a string value`() {

        "{\"a\":\"a\"}".jsonStructure succeedsAndShouldReturn listOf(
                ObjectOpen(id = 1),
                StringOpen(id = 2),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 2),
                ObjectColon(id = 1),
                StringOpen(id = 3),
                StringValue(id = 3, value = 'a'),
                StringClose(id = 3),
                ObjectClose(id = 1)
        )
    }


    @Test
    fun `it creates a simple object with an empty string key and value`() {

        "{\"\":\"\"}".jsonStructure succeedsAndShouldReturn listOf(
                ObjectOpen(id = 1),
                StringOpen(id = 2),
                StringClose(id = 2),
                ObjectColon(id = 1),
                StringOpen(id = 3),
                StringClose(id = 3),
                ObjectClose(id = 1)
        )
    }

    @Test
    fun `it can create an object with a string child and whitespace`() {

        forEachWhitespaceChar { char ->

            "{$char\"a\"$char:$char\"a\"$char}".jsonStructure succeedsAndShouldReturn listOf(
                    ObjectOpen(id = 1),
                    StringOpen(id = 2),
                    StringValue(id = 2, value = 'a'),
                    StringClose(id = 2),
                    ObjectColon(id = 1),
                    StringOpen(id = 3),
                    StringValue(id = 3, value = 'a'),
                    StringClose(id = 3),
                    ObjectClose(id = 1)
            )
        }
    }

    @Test
    fun `it can create an object with an empty array child`() {

        "{\"a\":[]}".jsonStructure succeedsAndShouldReturn listOf(
                ObjectOpen(id = 1),
                StringOpen(id = 2),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 2),
                ObjectColon(id = 1),
                ArrayOpen(id = 3),
                ArrayClose(id = 3),
                ObjectClose(id = 1)
        )
    }

    @Test
    fun `it can create an object with an empty array child and whitespace`() {

        forEachWhitespaceChar { char ->

            "{$char\"a\"$char:$char[$char]$char}"
                    .jsonStructure succeedsAndShouldReturn listOf(
                    ObjectOpen(id = 1),
                    StringOpen(id = 2),
                    StringValue(id = 2, value = 'a'),
                    StringClose(id = 2),
                    ObjectColon(id = 1),
                    ArrayOpen(id = 3),
                    ArrayClose(id = 3),
                    ObjectClose(id = 1)
            )
        }
    }

    @Test
    fun `it can create an object with an array child with a literal`() {

        "{\"a\":[a]}".jsonStructure succeedsAndShouldReturn listOf(
                ObjectOpen(id = 1),
                StringOpen(id = 2),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 2),
                ObjectColon(id = 1),
                ArrayOpen(id = 3),
                LiteralClose(id = 4, value = 'a'),
                ArrayClose(id = 3),
                ObjectClose(id = 1)
        )
    }

    @Test
    fun `it can create an object with an array child with an empty string`() {

        "{\"a\":[\"\"]}".jsonStructure succeedsAndShouldReturn listOf(
                ObjectOpen(id = 1),
                StringOpen(id = 2),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 2),
                ObjectColon(id = 1),
                ArrayOpen(id = 3),
                StringOpen(id = 4),
                StringClose(id = 4),
                ArrayClose(id = 3),
                ObjectClose(id = 1)
        )
    }

    @Test
    fun `it can create an object with an array child with an empty array`() {

        "{\"a\":[[]]}".jsonStructure succeedsAndShouldReturn listOf(
                ObjectOpen(id = 1),
                StringOpen(id = 2),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 2),
                ObjectColon(id = 1),
                ArrayOpen(id = 3),
                ArrayOpen(id = 4),
                ArrayClose(id = 4),
                ArrayClose(id = 3),
                ObjectClose(id = 1)
        )
    }

    @Test
    fun `it can create an object with an array child with an empty object`() {

        "{\"a\":[{}]}".jsonStructure succeedsAndShouldReturn listOf(
                ObjectOpen(id = 1),
                StringOpen(id = 2),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 2),
                ObjectColon(id = 1),
                ArrayOpen(id = 3),
                ObjectOpen(id = 4),
                ObjectClose(id = 4),
                ArrayClose(id = 3),
                ObjectClose(id = 1)
        )
    }

    @Test
    fun `it can create an object with an array child with a literal with whitespace`() {

        forEachWhitespaceChar { char ->

            "{\"a\":[${char}a$char]}".jsonStructure succeedsAndShouldReturn listOf(
                    ObjectOpen(id = 1),
                    StringOpen(id = 2),
                    StringValue(id = 2, value = 'a'),
                    StringClose(id = 2),
                    ObjectColon(id = 1),
                    ArrayOpen(id = 3),
                    LiteralClose(id = 4, value = 'a'),
                    ArrayClose(id = 3),
                    ObjectClose(id = 1)
            )
        }
    }

    @Test
    fun `it can create an object with an empty object child`() {

        "{\"a\":{}}".jsonStructure succeedsAndShouldReturn listOf(
                ObjectOpen(id = 1),
                StringOpen(id = 2),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 2),
                ObjectColon(id = 1),
                ObjectOpen(id = 3),
                ObjectClose(id = 3),
                ObjectClose(id = 1)
        )
    }

    @Test
    fun `it can create an object with an empty object child with whitespace`() {

        forEachWhitespaceChar { char ->

            "{\"a\":$char{$char}$char}".jsonStructure succeedsAndShouldReturn listOf(
                    ObjectOpen(id = 1),
                    StringOpen(id = 2),
                    StringValue(id = 2, value = 'a'),
                    StringClose(id = 2),
                    ObjectColon(id = 1),
                    ObjectOpen(id = 3),
                    ObjectClose(id = 3),
                    ObjectClose(id = 1)
            )
        }
    }

    @Test
    fun `it can create an object with an object child with a literal value`() {

        "{\"a\":{\"a\":a}}".jsonStructure succeedsAndShouldReturn listOf(
                ObjectOpen(id = 1),
                StringOpen(id = 2),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 2),
                ObjectColon(id = 1),
                ObjectOpen(id = 3),
                StringOpen(id = 4),
                StringValue(id = 4, value = 'a'),
                StringClose(id = 4),
                ObjectColon(id = 3),
                LiteralClose(id = 5, value = 'a'),
                ObjectClose(id = 3),
                ObjectClose(id = 1)
        )
    }

    @Test
    fun `it can create an object with an object child with a literal value with whitespace`() {

        forEachWhitespaceChar { char ->

            "{\"a\":{\"a\":${char}a$char}}".jsonStructure succeedsAndShouldReturn listOf(
                    ObjectOpen(id = 1),
                    StringOpen(id = 2),
                    StringValue(id = 2, value = 'a'),
                    StringClose(id = 2),
                    ObjectColon(id = 1),
                    ObjectOpen(id = 3),
                    StringOpen(id = 4),
                    StringValue(id = 4, value = 'a'),
                    StringClose(id = 4),
                    ObjectColon(id = 3),
                    LiteralClose(id = 5, value = 'a'),
                    ObjectClose(id = 3),
                    ObjectClose(id = 1)
            )
        }
    }

    @Test
    fun `it can create an object with an object child with a string value`() {

        "{\"a\":{\"a\":\"a\"}}".jsonStructure succeedsAndShouldReturn listOf(
                ObjectOpen(id = 1),
                StringOpen(id = 2),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 2),
                ObjectColon(id = 1),
                ObjectOpen(id = 3),
                StringOpen(id = 4),
                StringValue(id = 4, value = 'a'),
                StringClose(id = 4),
                ObjectColon(id = 3),
                StringOpen(id = 5),
                StringValue(id = 5, value = 'a'),
                StringClose(id = 5),
                ObjectClose(id = 3),
                ObjectClose(id = 1)
        )
    }

    @Test
    fun `it can create an object with an object child with an empty array value`() {

        "{\"a\":{\"a\":[]}}".jsonStructure succeedsAndShouldReturn listOf(
                ObjectOpen(id = 1),
                StringOpen(id = 2),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 2),
                ObjectColon(id = 1),
                ObjectOpen(id = 3),
                StringOpen(id = 4),
                StringValue(id = 4, value = 'a'),
                StringClose(id = 4),
                ObjectColon(id = 3),
                ArrayOpen(id = 5),
                ArrayClose(id = 5),
                ObjectClose(id = 3),
                ObjectClose(id = 1)
        )
    }

    @Test
    fun `it can create an object with an object child with an empty object value`() {

        "{\"a\":{\"a\":{}}}".jsonStructure succeedsAndShouldReturn listOf(
                ObjectOpen(id = 1),
                StringOpen(id = 2),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 2),
                ObjectColon(id = 1),
                ObjectOpen(id = 3),
                StringOpen(id = 4),
                StringValue(id = 4, value = 'a'),
                StringClose(id = 4),
                ObjectColon(id = 3),
                ObjectOpen(id = 5),
                ObjectClose(id = 5),
                ObjectClose(id = 3),
                ObjectClose(id = 1)
        )
    }

    @Test
    fun `it can create an object with multiple literal children`() {

        "{\"a\":a,\"b\":b}".jsonStructure succeedsAndShouldReturn listOf(
                ObjectOpen(id = 1),
                StringOpen(id = 2),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 2),
                ObjectColon(id = 1),
                LiteralClose(id = 3, value = 'a'),
                ObjectComma(id = 1),
                StringOpen(id = 4),
                StringValue(id = 4, value = 'b'),
                StringClose(id = 4),
                ObjectColon(id = 1),
                LiteralClose(id = 5, value = 'b'),
                ObjectClose(id = 1)
        )
    }

    @Test
    fun `it can create an object with multiple children and whitespace`() {

        forEachWhitespaceChar { char ->

            "{\"a\":a$char,$char\"b\":b}".jsonStructure succeedsAndShouldReturn listOf(
                    ObjectOpen(id = 1),
                    StringOpen(id = 2),
                    StringValue(id = 2, value = 'a'),
                    StringClose(id = 2),
                    ObjectColon(id = 1),
                    LiteralClose(id = 3, value = 'a'),
                    ObjectComma(id = 1),
                    StringOpen(id = 4),
                    StringValue(id = 4, value = 'b'),
                    StringClose(id = 4),
                    ObjectColon(id = 1),
                    LiteralClose(id = 5, value = 'b'),
                    ObjectClose(id = 1)
            )
        }
    }

    @Test
    fun `it can create an object with multiple string children`() {

        "{\"a\":\"a\",\"b\":\"b\"}".jsonStructure succeedsAndShouldReturn listOf(
                ObjectOpen(id = 1),
                StringOpen(id = 2),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 2),
                ObjectColon(id = 1),
                StringOpen(id = 3),
                StringValue(id = 3, value = 'a'),
                StringClose(id = 3),
                ObjectComma(id = 1),
                StringOpen(id = 4),
                StringValue(id = 4, value = 'b'),
                StringClose(id = 4),
                ObjectColon(id = 1),
                StringOpen(id = 5),
                StringValue(id = 5, value = 'b'),
                StringClose(id = 5),
                ObjectClose(id = 1)
        )
    }


    @Test
    fun `it can create an object with multiple empty array children`() {

        "{\"a\":[],\"b\":[]}".jsonStructure succeedsAndShouldReturn listOf(
                ObjectOpen(id = 1),
                StringOpen(id = 2),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 2),
                ObjectColon(id = 1),
                ArrayOpen(id = 3),
                ArrayClose(id = 3),
                ObjectComma(id = 1),
                StringOpen(id = 4),
                StringValue(id = 4, value = 'b'),
                StringClose(id = 4),
                ObjectColon(id = 1),
                ArrayOpen(id = 5),
                ArrayClose(id = 5),
                ObjectClose(id = 1)
        )
    }

    @Test
    fun `it can create an object with multiple empty object children`() {

        "{\"a\":{},\"b\":{}}".jsonStructure succeedsAndShouldReturn listOf(
                ObjectOpen(id = 1),
                StringOpen(id = 2),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 2),
                ObjectColon(id = 1),
                ObjectOpen(id = 3),
                ObjectClose(id = 3),
                ObjectComma(id = 1),
                StringOpen(id = 4),
                StringValue(id = 4, value = 'b'),
                StringClose(id = 4),
                ObjectColon(id = 1),
                ObjectOpen(id = 5),
                ObjectClose(id = 5),
                ObjectClose(id = 1)
        )
    }
}