package com.hcsc.de.claims.jsonReduction.cinqFieldCsv

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.jsonReduction.FieldObject

interface FieldObjectCsvFileReader {

    fun read(filePath: String): Result<String, FieldObject>
}