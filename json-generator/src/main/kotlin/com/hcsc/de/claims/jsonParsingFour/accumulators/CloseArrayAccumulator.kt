package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.jsonParsingFour.*

abstract class CloseArrayAccumulator<out closeType: Close> :  BaseAccumulator<closeType, ArrayStructureElement, MainStructure<*>>(){

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            ',' -> addElement(::ArrayComma)
            ']' -> when (enclosingStructure) {
                is EmptyStructureElement -> closeStructure(::ArrayClose)
                is ArrayStructureElement -> closeStructure(::ArrayClose)
                is ObjectWithKeyStructure -> closeStructure(::ArrayClose)
                is LiteralStructureElement -> TODO("SHOULD NOT HAPPEN")
                is StringStructureElement -> TODO("SHOULD NOT HAPPEN")
                is OpenObjectStructure -> TODO("SHOULD NOT HAPPEN")
            }
            else -> TODO()
        }
    }
}