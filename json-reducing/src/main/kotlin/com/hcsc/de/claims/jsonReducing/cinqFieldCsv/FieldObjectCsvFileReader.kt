package com.hcsc.de.claims.jsonReducing.cinqFieldCsv

import com.hcsc.de.claims.jsonReducing.FieldObject

interface FieldObjectCsvFileReader {

    fun read(filePath: String): com.hcsc.de.claims.results.Result<String, FieldObject>
}