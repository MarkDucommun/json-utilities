package com.hcsc.de.claims.fileReaders

import com.hcsc.de.claims.helpers.Result

interface StringFileReader {

    fun read(filePath: String): Result<String, String>
}