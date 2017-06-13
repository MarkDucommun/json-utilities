package com.hcsc.de.claims.jsonReduction.cinqFieldCsv

import com.hcsc.de.claims.jsonReduction.FieldObject
import com.hcsc.de.claims.results.Result

interface FieldObjectCsvFileReader {

    fun read(filePath: String): Result<String, FieldObject>
}