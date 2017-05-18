package com.hcsc.de.claims.fileReaders

class FunctionalCsvStringToFieldList : CsvStringToFieldList {

    override fun convert(csvString: String): List<List<String>> {

        return csvString.lines.map { it.process() }
    }

    private fun String.process() = split(",").dropLast(1).let { it[0].split(".").plus(it[1]) }

    private val String.lines: List<String> get() = split("\n")
}