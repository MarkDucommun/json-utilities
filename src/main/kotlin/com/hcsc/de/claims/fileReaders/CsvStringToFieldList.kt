package com.hcsc.de.claims.fileReaders

interface CsvStringToFieldList {

    fun convert(csvString: String): List<List<String>>
}