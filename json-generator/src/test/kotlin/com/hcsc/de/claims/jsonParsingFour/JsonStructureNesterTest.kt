package com.hcsc.de.claims.jsonParsingFour

import com.hcsc.de.claims.succeedsAnd
import com.hcsc.de.claims.succeedsAndShouldReturn
import org.junit.Assert.*
import org.junit.Test

class JsonStructureNesterTest {

    @Test
    fun `it returns an empty StringStructureElement`() {

        listOf(StringOpen(id = 1), StringClose(id = 1)).nested succeedsAndShouldReturn StringStructureElement(id = 1, children = emptyList())
    }

    @Test
    fun `it returns an StringStructureElement with some values`() {

        listOf(StringOpen(id = 1), StringClose(id = 1)).nested succeedsAndShouldReturn StringStructureElement(id = 1, children = emptyList())
    }

    private val subject = JsonStructureNester()

    private val List<JsonStructure>.nested get() = subject.nest(this)
}