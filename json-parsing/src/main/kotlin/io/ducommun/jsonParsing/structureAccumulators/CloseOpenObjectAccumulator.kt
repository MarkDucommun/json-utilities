package io.ducommun.jsonParsing.structureAccumulators

import com.hcsc.de.claims.results.Result
import io.ducommun.jsonParsing.*

abstract class CloseOpenObjectAccumulator<out closeType: Close> : BaseAccumulator<closeType, OpenObjectStructure, ObjectChildElement<*>>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> skip
            ',' -> addElement(::ObjectComma)
            '}' -> when (enclosingStructure) {
                is EmptyStructureElement -> closeStructure(::ObjectClose)
                is ArrayStructureElement -> closeStructure(::ObjectClose)
                is ObjectWithKeyStructure -> closeStructure(::ObjectClose)
                is LiteralStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is StringStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is OpenObjectStructure -> TODO("THIS SHOULD NEVER HAPPEN")
            }
            else -> fail("must close an object with a curly brace")
        }
    }
}