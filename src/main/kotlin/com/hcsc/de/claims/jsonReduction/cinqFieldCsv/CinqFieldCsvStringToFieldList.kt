package com.hcsc.de.claims.jsonReduction.cinqFieldCsv

import com.hcsc.de.claims.Translator
import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success

class CinqFieldCsvStringToFieldList : Translator<String, ListOfFieldList> {

    override fun translate(csvString: String): Result<String, ListOfFieldList> {

        return try {
            Success(csvString.lines.map { it.process() })
        } catch (e: Exception) {
            Failure("Some weird parsing error happened reading the Cinq Field CSV")
        }
    }

    private fun String.process() = split(",").dropLast(1).let { it[0].split(".").plus(it[1]) }

    private val String.lines: List<String> get() = split("\n")
}