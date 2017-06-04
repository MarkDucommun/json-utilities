package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.jsonParsingFour.*

abstract class EmptyAccumulator<out previousElementType : JsonStructure>
    : BaseAccumulator<previousElementType, EmptyStructureElement>() {

    abstract val elementName: String

    override val previousClosable: EmptyStructureElement = EmptyStructureElement

    override val structureStack: List<MainStructure> = listOf(EmptyStructureElement)

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            else -> fail("nothing can follow a closed root $elementName")
        }
    }
}