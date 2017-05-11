package com.hcsc.de.claims

import java.io.File

fun FilePath.readAndConvertCsvToFieldSetList(): List<List<String>> {

    return String(File(this).readBytes())
            .split("\n")
            .map { row -> row.split(",").dropLast(1).let { it[0].split(".").plus(it[1]) } }
}

typealias FilePath = String