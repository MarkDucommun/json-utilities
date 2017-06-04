package com.hcsc.de.claims.jsonParsingFour

abstract class JsonStructureParserBaseTest {

    protected val subject = JsonStructureParser()

    protected val String.jsonStructure get() = subject.parse(this)

    protected fun forEachWhitespaceChar(fn: (Char) -> Unit) {
        WHITESPACE_CHARS.forEach(fn)
    }

    private val WHITESPACE_CHARS = listOf(' ', '\n', '\t', '\r')
}
