package com.hcsc.de.claims.jsonReducing.cinqFieldCsv

import com.hcsc.de.claims.Translator
import com.hcsc.de.claims.jsonReduction.FieldObject

internal class CinqFieldCsvStringToFieldObject(
        private val cinqFieldCsvStringToFieldList: Translator<String, ListOfFieldList>,
        private val fieldListToFieldObject: Translator<ListOfFieldList, FieldObject>
) : Translator<String, FieldObject> {

    override fun translate(input: String): com.hcsc.de.claims.results.Result<String, FieldObject> =
            cinqFieldCsvStringToFieldList
                    .translate(input)
                    .flatMap { fieldListToFieldObject.translate(it) }
}