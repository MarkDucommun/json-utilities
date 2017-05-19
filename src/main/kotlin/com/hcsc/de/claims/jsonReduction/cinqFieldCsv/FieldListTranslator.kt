package com.hcsc.de.claims.jsonReduction.cinqFieldCsv

import com.hcsc.de.claims.Translator
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.jsonReduction.FieldObject

class FieldListTranslator : Translator<ListOfFieldList, FieldObject> {

    override fun translate(input: ListOfFieldList): Result<String, FieldObject> {

        val fieldObjects = input.map { it.fieldObjects }.filterNotNull().groupAndCombine()

        return Success(FieldObject(name = "root", properties = fieldObjects))
    }

    private fun List<FieldObject>.groupAndCombine(): List<FieldObject> {

        return groupBy { it.name }.map { (name, fieldObjects) ->

            FieldObject(name = name, properties = fieldObjects.flatMap { it.properties }.groupAndCombine())
        }
    }

    private val FieldList.fieldObjects: FieldObject? get() {

        return firstOrNull()?.let { name ->

            val properties = listOf(subList(1, size).fieldObjects).filterNotNull()

            FieldObject(name = name, properties = properties)
        }
    }
}
