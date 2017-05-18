package com.hcsc.de.claims.fileReaders

import com.hcsc.de.claims.jsonReduction.FieldObject

interface FieldObjectCsvFileReader {

    fun read(filePath: String): FieldObject
}