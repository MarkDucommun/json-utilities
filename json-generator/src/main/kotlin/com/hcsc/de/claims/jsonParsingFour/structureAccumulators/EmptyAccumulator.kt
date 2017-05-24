package com.hcsc.de.claims.jsonParsingFour.structureAccumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.jsonParsingFour.*

abstract class EmptyAccumulator<out previousElementType : JsonStructure>
    : BaseAccumulator<previousElementType, EmptyStructureElement, EmptyStructureElement>() {

    abstract val elementName: String

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            ' ', '\n', '\r', '\t' -> skip
            else -> fail("nothing can follow a closed root $elementName")
        }
    }
}