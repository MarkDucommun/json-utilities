package com.hcsc.de.claims.fileReading

import com.hcsc.de.claims.results.Result
import java.io.File

interface StringFileReader {

    fun read(filePath: String): Result<String, String>

    fun read(file: File): Result<String, String>
}