package com.hcsc.de.claims.fileReading

import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.wrapExternalLibraryUsageAsResultWithFailureMessage
import java.io.File

class RawByteStringFileReader : StringFileReader {

    override fun read(file: File): Result<String, String> =
            wrapExternalLibraryUsageAsResultWithFailureMessage("File not found") { String(file.readBytes()) }

    override fun read(filePath: String): Result<String, String> = read(File(filePath))
}