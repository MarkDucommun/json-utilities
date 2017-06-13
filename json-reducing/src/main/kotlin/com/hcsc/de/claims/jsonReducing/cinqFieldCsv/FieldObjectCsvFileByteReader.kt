package com.hcsc.de.claims.jsonReducing.cinqFieldCsv

import com.hcsc.de.claims.Translator
import com.hcsc.de.claims.fileReaders.StringFileReader
import com.hcsc.de.claims.jsonReduction.FieldObject

class FieldObjectCsvFileByteReader(
        private val fileReader: StringFileReader,
        private val csvStringConverter: Translator<String, FieldObject>
) : FieldObjectCsvFileReader {

    override fun read(filePath: String): com.hcsc.de.claims.results.Result<String, FieldObject> =
            fileReader.read(filePath).flatMap { csvStringConverter.translate(it) }
}
