package com.hcsc.de.claims.jsonParsingFour

import com.hcsc.de.claims.failsWithMessage
import com.hcsc.de.claims.succeedsAndShouldReturn
import org.junit.Test

class JsonStructureParserLiteralTest : JsonStructureParserBaseTest() {

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

        forEachWhitespaceChar { char ->

            "${char}a$char".jsonStructure succeedsAndShouldReturn listOf(LiteralClose(id = 1, value = 'a'))
        }
    }

    @Test
    fun `it fails when there is a literal following a closed literal`() {

        "a a".jsonStructure failsWithMessage "Invalid JSON - nothing can follow a closed root literal"
    }
}