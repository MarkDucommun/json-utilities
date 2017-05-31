package com.hcsc.de.claims.jsonParsing

import com.hcsc.de.claims.failsWithMessage
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.succeedsAndShouldReturn
import org.junit.Test

class LinearJsonStructureParserTest {

    val subject = LinearJsonStructureParser()

    val String.jsonStructure: Result<String, List<JsonStructureElement>> get() = subject.parse(this)

    @Test
    fun `1 - it adds a literal when there is no current closable and no previous element and the character is non-special, non-whitespace`() {

        "a".jsonStructure succeedsAndShouldReturn listOf(Literal('a'))
    }

    @Test
    fun `2 - it adds whitespace when there is no current closable and no previous element`() {

        listOf(" ", "\n", "\t", "\r").forEach { it.jsonStructure succeedsAndShouldReturn listOf(Whitespace) }
    }

    @Test
    fun `3 - it fails when there is no current closable and no previous element and the char is invalid`() {

        listOf("}", "]", ",", ":", "\\", "/")
                .forEach { it.jsonStructure failsWithMessage "Invalid JSON: '$it' may not begin a JSON document" }
    }

    @Test
    fun `4 - it can open and close an empty String`() {

        "\"\"".jsonStructure succeedsAndShouldReturn listOf(
                ElementStart(StringElement(id = 0)),
                ElementEnd(StringElement(id = 0)))
    }

    @Test
    fun `5 - it can open and close an empty Array`() {

        "[]".jsonStructure succeedsAndShouldReturn listOf(
                ElementStart(ArrayElement(id = 0)),
                ElementEnd(ArrayElement(id = 0)))
    }

    @Test
    fun `6 - it can open and close an empty Object`() {

        "{}".jsonStructure succeedsAndShouldReturn listOf(
                ElementStart(ObjectElement(id = 0)),
                ElementEnd(ObjectElement(id = 0)))
    }

    @Test
    fun `7 - it adds whitespace when there is no current closable and the previous element is a literal`() {

        "a ".jsonStructure succeedsAndShouldReturn listOf(
                Literal('a'),
                Whitespace
        )
    }

    @Test
    fun `8 - it adds a literal when there is no current closable and the previous element is a literal`() {

        "ab".jsonStructure succeedsAndShouldReturn listOf(
                Literal('a'),
                Literal('b')
        )
    }

    @Test
    fun `9 - it fails when there is no current closable and the previous element is a literal and the char is invalid`() {

        listOf("\"", "{", "}", "[", "]", ",", ":", "\\", "/").forEach {
            "a$it".jsonStructure failsWithMessage "Invalid JSON: '$it' may not follow a literal character in a JSON document"
        }
    }

    @Test
    fun `10 - it adds whitespace after any closable end when there is no current closable`() {

        val whitespaceChars = listOf(" ", "\n", "\t", "\r")

        whitespaceChars.forEach { whitespaceChar ->
            "{}$whitespaceChar".jsonStructure succeedsAndShouldReturn listOf(
                    ElementStart(ObjectElement(id = 0)),
                    ElementEnd(ObjectElement(id = 0)),
                    Whitespace)
        }

        whitespaceChars.forEach { whitespaceChar ->
            "[]$whitespaceChar".jsonStructure succeedsAndShouldReturn listOf(
                    ElementStart(ArrayElement(id = 0)),
                    ElementEnd(ArrayElement(id = 0)),
                    Whitespace)
        }

        whitespaceChars.forEach { whitespaceChar ->
            "\"\"$whitespaceChar".jsonStructure succeedsAndShouldReturn listOf(
                    ElementStart(StringElement(id = 0)),
                    ElementEnd(StringElement(id = 0)),
                    Whitespace)
        }
    }

    @Test
    fun `11 - when current closable is nothing and previous element is a closable end, any non-whitespace character fails`() {

        "{}a".jsonStructure failsWithMessage "Invalid JSON: 'a' may not follow a closable end at the end of a JSON document"
    }

    @Test
    fun `12 - whitespace is never considered a previous element`() {
        " \n\t\r".jsonStructure succeedsAndShouldReturn listOf(
                Whitespace,
                Whitespace,
                Whitespace,
                Whitespace
        )
    }

    @Test
    fun `13 - when current closable is a string, previous element is an open string and current char is a slash creates an escape character which must be followed by a quote or slash`() {

        "\"\\\"\"".jsonStructure succeedsAndShouldReturn listOf(
                ElementStart(StringElement(id = 0)),
                Escape,
                Literal('"'),
                ElementEnd(StringElement(id = 0))
        )

        "\"\\\\\"".jsonStructure succeedsAndShouldReturn listOf(
                ElementStart(StringElement(id = 0)),
                Escape,
                Literal('\\'),
                ElementEnd(StringElement(id = 0))
        )

        "\"\\/\"".jsonStructure succeedsAndShouldReturn listOf(
                ElementStart(StringElement(id = 0)),
                Escape,
                Literal('/'),
                ElementEnd(StringElement(id = 0))
        )
    }

    @Test
    fun `14 - when current closable is a string, previous element is an escape and current char is a non-escapable character `() {

        "\"\\a".jsonStructure failsWithMessage "Invalid JSON: 'a' may not follow an escape character"
    }

    @Test
    fun `15 - when current closable is a string, previous element is a string open and current char is a normal char, it adds a literal`() {

        "\"a\"".jsonStructure succeedsAndShouldReturn listOf(
                ElementStart(StringElement(id = 0)),
                Literal('a'),
                ElementEnd(StringElement(id = 0))
        )
    }

    @Test
    fun `16 - when current closable is a string, previous element is a literal and current char is a slash character, it adds an escape which must be followed by a escapable character`() {

        "\"a\\\"\"".jsonStructure succeedsAndShouldReturn listOf(
                ElementStart(StringElement(id = 0)),
                Literal('a'),
                Escape,
                Literal('"'),
                ElementEnd(StringElement(id = 0))
        )
    }

    @Test
    fun `17 - when current closable is a string, previous element is a literal and current char is a non-escapable character, it adds a literal`() {

        "\"ab\"".jsonStructure succeedsAndShouldReturn listOf(
                ElementStart(StringElement(id = 0)),
                Literal('a'),
                Literal('b'),
                ElementEnd(StringElement(id = 0))
        )
    }

    @Test
    fun `18 - when current closable is an array, previous element is the array open and the current char is a quote, it opens an array child string`() {

        "[\"\"]".jsonStructure succeedsAndShouldReturn listOf(
                ElementStart(ArrayElement(id = 0)),
                ElementStart(ArrayChildElement(StringElement(id = 1))),
                ElementEnd(ArrayChildElement(StringElement(id = 1))),
                ElementEnd(ArrayElement(id = 0))
        )
    }
}