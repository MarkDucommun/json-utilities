package com.hcsc.de.claims.fileReading

interface StringFileReader {

    fun read(filePath: String): com.hcsc.de.claims.results.Result<String, String>
}