package com.hcsc.de.claims.jsonReduction.cinqFieldCsv

import com.hcsc.de.claims.Translator
import com.hcsc.de.claims.failsWithMessage
import com.hcsc.de.claims.fileReaders.StringFileReader
import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.jsonReduction.FieldObject
import com.hcsc.de.claims.mockTranslator
import com.hcsc.de.claims.succeedsAndReturns
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class FieldObjectCsvFileByteReaderTest {

    @Test
    fun `it passes the file path to the file reader`() {

        val mockStringFileReader = mockStringFileReader()

        val subject = fieldObjectCsvFileByteReader(
                stringFileReader = mockStringFileReader
        )

        subject.read("file-path")

        verify(mockStringFileReader).read("file-path")
    }

    @Test
    fun `it passes the file string to the string to field object translator`() {

        val mockCsvStringTranslator = mockCsvStringTranslator()

        val subject = fieldObjectCsvFileByteReader(
                stringFileReader = mockStringFileReader(result = Success("string")),
                csvStringConverter = mockCsvStringTranslator
        )

        subject.read("file-path")

        verify(mockCsvStringTranslator).translate("string")
    }

    @Test
    fun `it returns the translated FieldObject`() {

        val expectedFieldObject = FieldObject(name = "worked!")

        val subject = fieldObjectCsvFileByteReader(
                csvStringConverter = mockCsvStringTranslator(result = Success(expectedFieldObject))
        )

        subject.read("file-path") succeedsAndReturns expectedFieldObject
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
    ): StringFileReader = mock{ on { read(any()) } doReturn result }

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