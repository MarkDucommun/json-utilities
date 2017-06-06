package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.jsonParsingFour.*

data class ObjectCommaAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: ObjectComma,
        override val previousClosable: OpenObjectStructure
) : BaseAccumulator<ObjectComma, OpenObjectStructure>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            '"' -> {

                val open = StringOpen(id = idCounter)

                val string = StringStructureElement(id = idCounter)

                Success(StringOpenAccumulator(
                        idCounter = idCounter + 1,
                        structure = structure.plus(open),
                        structureStack = structureStack.plus(string),
                        previousElement = open,
                        previousClosable = string
                ))
            }
            else -> TODO()
//            else -> fail("object key must be a string")
        }
    }
}