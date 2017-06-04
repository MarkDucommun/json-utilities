package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.jsonParsingFour.*

data class StringEscapeAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: StringEscape,
        override val previousClosable: StringStructureElement
) : BaseAccumulator<StringEscape, StringStructureElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            '"', '\\', '/' -> {

                val stringElement = StringValue(id = previousClosable.id, value = char)

                Success<String, Accumulator<*, *>>(StringValueAccumulator(
                        idCounter = idCounter,
                        structureStack = structureStack,
                        previousElement = stringElement,
                        structure = structure.plus(stringElement),
                        previousClosable = previousClosable
                ))
            }
            else -> fail("only quotes and slashes may follow escape characters")
        }
    }
}