package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.jsonParsingFour.*

data class ObjectWithKeyAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: StringClose,
        override val previousClosable: ObjectWithKeyStructure
) : BaseAccumulator<StringClose, ObjectWithKeyStructure>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> unmodified
            ':' -> {

                val colon = ObjectColon(id = previousClosable.id)

                Success(ObjectReadyForValueAccumulator(
                        idCounter = idCounter,
                        structure = structure.plus(colon),
                        structureStack = structureStack,
                        previousElement = colon,
                        previousClosable = previousClosable
                ))
            }
            else -> fail("colon must follow an object key")
        }
    }
}