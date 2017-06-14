package com.hcsc.de.claims.jsonReducing.cinqFieldCsv

import com.hcsc.de.claims.commonAbstractions.Translator

class CinqFieldCsvStringToFieldList : Translator<String, ListOfFieldList> {

    override fun translate(csvString: String): com.hcsc.de.claims.results.Result<String, ListOfFieldList> {

        return try {
            com.hcsc.de.claims.results.Success(csvString.lines.map { it.process() })
        } catch (e: Exception) {
            com.hcsc.de.claims.results.Failure("Some weird parsing error happened reading the Cinq Field CSV")
        }
    }

    private fun String.process() = split(",").dropLast(1).let { it[0].split(".").plus(it[1]) }

    private val String.lines: List<String> get() = split("\n")
}