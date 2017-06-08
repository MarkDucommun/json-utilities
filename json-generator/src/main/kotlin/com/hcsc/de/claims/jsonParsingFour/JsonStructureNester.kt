package com.hcsc.de.claims.jsonParsingFour

import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.helpers.flatMap

class JsonStructureNester {

    fun nest(structure: List<JsonStructure>): Result<String, MainStructure<*>> {

        val first = structure.first()
        val last = structure.last()

        return if (first is Open<*, *> && last is Close && first.id == last.id) {

            val nestedStructure = first.structureConstructor.invoke(first.id, emptyList())

            when (nestedStructure) {
                is EmptyStructureElement -> TODO()
                is LiteralStructureElement -> TODO()
                is StringStructureElement -> Success<String, MainStructure<*>>(
                        nestedStructure.copy(
                                children = structure
                                        .drop(1)
                                        .dropLast(1)
                                        .filterIsInstance(StringValue::class.java)
                        )
                )
                is ArrayStructureElement -> TODO()
                is OpenObjectStructure, is ObjectWithKeyStructure -> TODO()
            }
        } else {
            Failure("Start and end of structure don't match")
        }
    }
}