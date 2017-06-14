package com.hcsc.de.claims.jsonReducing

import com.hcsc.de.claims.commonAbstractions.Translator
import com.hcsc.de.claims.commonAbstractionsTestHelpers.mockTranslator
import com.hcsc.de.claims.fileReading.StringFileReader
import com.hcsc.de.claims.jsonReducing.cinqFieldCsv.FieldObjectCsvFileByteReader
import com.hcsc.de.claims.results.*
import com.hcsc.de.claims.results.failsWithMessage
import com.hcsc.de.claims.results.succeedsAndShouldReturn
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test

class FieldObjectCsvFileByteReaderTest {

    @Test
    fun `it passes the file path to the file reader`() {

        val mockStringFileReader = mockStringFileReader()

        val subject = fieldObjectCsvFileByteReader(
                stringFileReader = mockStringFileReader
        )

        subject.read("file-path")

        com.nhaarman.mockito_kotlin.verify(mockStringFileReader).read("file-path")
    }

    @Test
    fun `it passes the file string to the string to field object translator`() {

        val mockCsvStringTranslator = mockCsvStringTranslator()

        val subject = fieldObjectCsvFileByteReader(
                stringFileReader = mockStringFileReader(result = Success("string")),
                csvStringConverter = mockCsvStringTranslator
        )

        subject.read("file-path")

        com.nhaarman.mockito_kotlin.verify(mockCsvStringTranslator).translate("string")
    }

    @Test
    fun `it returns the translated FieldObject`() {

        val expectedFieldObject = FieldObject(name = "worked!")

        val subject = fieldObjectCsvFileByteReader(
                csvStringConverter = mockCsvStringTranslator(result = Success(expectedFieldObject))
        )

        subject.read("file-path") succeedsAndShouldReturn expectedFieldObject
    }

    @Test
    fun `it returns the failed file read`() {

        val subject = fieldObjectCsvFileByteReader(
                stringFileReader = mockStringFileReader(Failure("I failed"))
        )

        subject.read("file") failsWithMessage "I failed"
    }

    @Test
    fun `it returns the failed string translation`() {

        val subject = fieldObjectCsvFileByteReader(
                csvStringConverter = mockCsvStringTranslator(Failure("I failed"))
        )

        subject.read("file") failsWithMessage "I failed"
    }

    private fun mockStringFileReader(
            result: Result<String, String> = Success("")
    ): StringFileReader = mock { on { read(filePath = any()) } doReturn result }

    private fun mockCsvStringTranslator(
            result: Result<String, FieldObject> = Success(FieldObject("default"))
    ): Translator<String, FieldObject> = mockTranslator(result)

    private fun fieldObjectCsvFileByteReader(
            stringFileReader: StringFileReader = mockStringFileReader(),
            csvStringConverter: Translator<String, FieldObject> = mockCsvStringTranslator()
    ) = FieldObjectCsvFileByteReader(
            fileReader = stringFileReader,
            csvStringConverter = csvStringConverter
    )
}