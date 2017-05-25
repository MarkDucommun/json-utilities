package com.hcsc.de.claims.jsonParsing

import com.hcsc.de.claims.failsWithMessage
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.succeedsAndShouldReturn
import com.sun.tools.javadoc.Start
import org.junit.Assert.*
import org.junit.Test

class LinearJsonStructureParserTest {

    val subject = LinearJsonStructureParser()

    val String.jsonStructure: Result<String, List<JsonStructureElement>> get() = subject.parse(this)

    @Test
    fun `it can create a simple structure`() {

        "asdf".jsonStructure succeedsAndShouldReturn listOf(
                Literal('a'),
                Literal('s'),
                Literal('d'),
                Literal('f')
        )
    }

    @Test
    fun `it can create a simple String structure`() {

        "\"asdf\"".jsonStructure succeedsAndShouldReturn listOf(
                ElementStart(StringElement(id = 0)),
                Literal('a'),
                Literal('s'),
                Literal('d'),
                Literal('f'),
                ElementEnd(StringElement(id = 0)
        ))
    }

    @Test
    fun `it can create a simple Array structure`() {

        "[asdf]".jsonStructure succeedsAndShouldReturn listOf(
                ElementStart(ArrayElement(id = 0)),
                Literal('a'),
                Literal('s'),
                Literal('d'),
                Literal('f'),
                ElementEnd(ArrayElement(id = 0)
        ))
    }

    @Test
    fun `it can create an Array with two children`() {

        "[asdf,asdf]".jsonStructure succeedsAndShouldReturn listOf(
                ElementStart(ArrayElement(id = 0)),
                Literal('a'),
                Literal('s'),
                Literal('d'),
                Literal('f'),
                CommaElement,
                Literal('a'),
                Literal('s'),
                Literal('d'),
                Literal('f'),
                ElementEnd(ArrayElement(id = 0))
        )
    }

    @Test
    fun `it fails if a comma is used outside of an enclosing object`() {

        "as,df".jsonStructure failsWithMessage "Invalid JSON, cannot use comma outside of an object, string or array"
    }

    @Test
    fun `it fails if a previous string has not been closed and an array is attempted to be closed`() {

        "[\"asdf]".jsonStructure failsWithMessage "Invalid JSON, attempted to close array before string was closed"
    }

    @Test
    fun `it fails if an object has not been opened and an array is attempted to be closed`() {

        "asdf]".jsonStructure failsWithMessage "Invalid JSON, array was never opened"
    }

    @Test
    fun `it can parse an object with a literal body`() {

        "{\"a\":1}".jsonStructure succeedsAndShouldReturn listOf(
                ElementStart(ObjectElement(id = 0)),
                ElementStart(ObjectKeyElement(id = 1)),
                Literal('a'),
                ElementEnd(ObjectKeyElement(id = 1)),
                ColonElement,
                Literal('1'),
                ElementEnd(ObjectElement(id = 0))
        )
    }

    @Test
    fun `it can parse an object with a non-literal body`() {

        "{\"a\":\"1\"}".jsonStructure succeedsAndShouldReturn listOf(
                ElementStart(ObjectElement(id = 0)),
                ElementStart(ObjectKeyElement(id = 1)),
                Literal('a'),
                ElementEnd(ObjectKeyElement(id = 1)),
                ColonElement,
                ElementStart(ObjectValueElement(StringElement(id = 2))),
                Literal('1'),
                ElementEnd(ObjectValueElement(StringElement(id = 2))),
                ElementEnd(ObjectElement(id = 0))
        )
    }

    @Test
    fun `it can parse an object with multiple children`() {

        "{\"a\":\"1\",\"b\":\"2\"}".jsonStructure succeedsAndShouldReturn listOf(
                ElementStart(ObjectElement(id = 0)),
                ElementStart(ObjectKeyElement(id = 1)),
                Literal('a'),
                ElementEnd(ObjectKeyElement(id = 1)),
                ColonElement,
                ElementStart(ObjectValueElement(StringElement(id = 2))),
                Literal('1'),
                ElementEnd(ObjectValueElement(StringElement(id = 2))),
                CommaElement,
                ElementStart(ObjectKeyElement(id = 3)),
                Literal('b'),
                ElementEnd(ObjectKeyElement(id = 3)),
                ColonElement,
                ElementStart(ObjectValueElement(StringElement(id = 4))),
                Literal('2'),
                ElementEnd(ObjectValueElement(StringElement(id = 4))),
                ElementEnd(ObjectElement(id = 0))
        )
    }

    @Test
    fun `it cannot parse an object with key that is not string`() {

        "{a:\"1\"}".jsonStructure failsWithMessage "Invalid JSON, object keys must be strings"
    }
}