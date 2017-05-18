package com.hcsc.de.claims.fileReaders

import java.io.File

class RawByteStringFileReader : StringFileReader {

    override fun read(filePath: String) = String(File(filePath).readBytes())
}