package com.hcsc.de.claims.jsonParsingFour.structureAccumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.jsonParsingFour.*

abstract class StringAccumulator<out previousElementType : StringElement>
    : BaseAccumulator<previousElementType, StringStructureElement, StringElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            // TODO can this just be closeString() without the when?
            '"' -> when (enclosingStructure) {
                is EmptyStructureElement -> closeString()
                is ArrayStructureElement -> closeString()
                is OpenObjectStructure -> closeString()
                is ObjectWithKeyStructure -> closeString()
                is LiteralStructureElement -> fail("How do I get rid of you as a possibility")
                is StringStructureElement -> fail("How do I get rid of you as a possibility")
            }
            '\\' -> addStringEscape()
            else -> addStringValue(char)
        }
    }

    fun addStringValue(char: Char): Result<String, Accumulator<*, *>> = addValue(::StringValue, char)

    fun addStringEscape(): Result<String, Accumulator<*, *>> = setPreviousElement(StringEscape)

    fun closeString(): Result<String, Accumulator<*, *>> = closeStructure(::StringClose)
}