package io.ducommun.jsonParsing.structureAccumulators

import com.hcsc.de.claims.results.Result
import io.ducommun.jsonParsing.EmptyStructureElement
import io.ducommun.jsonParsing.JsonStructure

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