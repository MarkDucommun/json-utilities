package com.hcsc.de.claims.fileReaders

import com.hcsc.de.claims.results.Result

interface StringFileReader {

    fun read(filePath: String): Result<String, String>
}