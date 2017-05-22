package com.hcsc.de.claims.jsonSizing

import com.fasterxml.jackson.databind.ObjectMapper
import com.hcsc.de.claims.fileReaders.RawByteStringFileReader
import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Success
import org.junit.Test
import java.io.FileWriter

class JsonSizeAnalyzerIntegrationTest {

    @Test
    fun `it reads in some files and creates a size analysis`() {

        val files = listOf(
                "src/test/resources/data/deidentifiedClaims1.json",
                "src/test/resources/data/deidentifiedClaims2.json",
                "src/test/resources/data/deidentifiedClaims3.json",
                "src/test/resources/data/deidentifiedClaims4.json"
        )

        val fileReader = RawByteStringFileReader()

        val rawClaims = files.flatMap {
            val result = fileReader.read(it)

            when (result) {
                is Success -> result.content.split("\n")
                is Failure -> throw RuntimeException(result.content)
            }
        }

        val jsonSizer = JsonSizer()

        val listOfJsonSizes = rawClaims.filterNot { it.isBlank() }.map {
            val result = jsonSizer.calculateSize(it)

            when (result) {
                is Success -> result.content
                is Failure -> throw RuntimeException(result.content)
            }
        }

        val jsonSizeAnalyzer = JsonSizeAnalyzer()

        val result = jsonSizeAnalyzer.generateJsonSizeOverview(listOfJsonSizes).blockingGet()

        when (result) {
            is Success -> FileWriter("claim-overview.json").apply {
                write(ObjectMapper().writeValueAsString(result.content))
                close()
            }
            is Failure -> throw RuntimeException(result.content)
        }
    }
}