package com.hcsc.de.claims.jsonReducing

import com.hcsc.de.claims.commonAbstractions.Translator
import com.hcsc.de.claims.commonAbstractionsTestHelpers.mockTranslator
import com.hcsc.de.claims.fileReading.StringFileReader
import com.hcsc.de.claims.jsonReducing.cinqFieldCsv.FieldObjectCsvFileByteReader
import com.hcsc.de.claims.results.failsWithMessage
import com.hcsc.de.claims.results.succeedsAndShouldReturn
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock

class FieldObjectCsvFileByteReaderTest {

    @org.junit.Test
    fun `it passes the file path to the file reader`() {

        val mockStringFileReader = mockStringFileReader()

        val subject = fieldObjectCsvFileByteReader(
                stringFileReader = mockStringFileReader
        )

        subject.read("file-path")

        com.nhaarman.mockito_kotlin.verify(mockStringFileReader).read("file-path")
    }

    @org.junit.Test
    fun `it passes the file string to the string to field object translator`() {

        val mockCsvStringTranslator = mockCsvStringTranslator()

        val subject = fieldObjectCsvFileByteReader(
                stringFileReader = mockStringFileReader(result = com.hcsc.de.claims.results.Success("string")),
                csvStringConverter = mockCsvStringTranslator
        )

        subject.read("file-path")

        com.nhaarman.mockito_kotlin.verify(mockCsvStringTranslator).translate("string")
    }

    @org.junit.Test
    fun `it returns the translated FieldObject`() {

        val expectedFieldObject = FieldObject(name = "worked!")

        val subject = fieldObjectCsvFileByteReader(
                csvStringConverter = mockCsvStringTranslator(result = com.hcsc.de.claims.results.Success(expectedFieldObject))
        )

        subject.read("file-path") succeedsAndShouldReturn expectedFieldObject
    }

    @org.junit.Test
    fun `it returns the failed file read`() {

        val subject = fieldObjectCsvFileByteReader(
                stringFileReader = mockStringFileReader(com.hcsc.de.claims.results.Failure("I failed"))
        )

        subject.read("file") failsWithMessage "I failed"
    }

    @org.junit.Test
    fun `it returns the failed string translation`() {

        val subject = fieldObjectCsvFileByteReader(
                csvStringConverter = mockCsvStringTranslator(com.hcsc.de.claims.results.Failure("I failed"))
        )

        subject.read("file") failsWithMessage "I failed"
    }

    private fun mockStringFileReader(
            result: com.hcsc.de.claims.results.Result<String, String> = com.hcsc.de.claims.results.Success("")
    ): StringFileReader = mock { on { read(any()) } doReturn result }

    private fun mockCsvStringTranslator(
            result: com.hcsc.de.claims.results.Result<String, FieldObject> = com.hcsc.de.claims.results.Success(FieldObject("default"))
    ): Translator<String, FieldObject> = mockTranslator(result)

    private fun fieldObjectCsvFileByteReader(
            stringFileReader: StringFileReader = mockStringFileReader(),
            csvStringConverter: Translator<String, FieldObject> = mockCsvStringTranslator()
    ) = FieldObjectCsvFileByteReader(
            fileReader = stringFileReader,
            csvStringConverter = csvStringConverter
    )
}