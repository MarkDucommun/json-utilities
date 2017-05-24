package com.hcsc.de.claims.fileReaders

import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import java.io.File

class RawByteStringFileReader : StringFileReader {

    override fun read(filePath: String): Result<String, String> =
            filePath.asFile.let { result ->
                when (result) {
                    is Success -> Success(String(result.content))
                    is Failure -> Failure(result.content)
                }
            }

    private val String.asFile: Result<String, ByteArray> get() = try {
        Success(File(this).readBytes())
    } catch (e: Exception) {
        Failure("File not found")
    }
}