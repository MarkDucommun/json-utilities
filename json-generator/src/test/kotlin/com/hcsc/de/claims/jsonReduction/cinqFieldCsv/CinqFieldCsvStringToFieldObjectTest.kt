package com.hcsc.de.claims.jsonReduction.cinqFieldCsv

import com.hcsc.de.claims.Translator
import com.hcsc.de.claims.jsonReduction.FieldObject
import com.hcsc.de.claims.mockTranslator
import com.hcsc.de.claims.results.*
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class CinqFieldCsvStringToFieldObjectTest {

    @Test
    fun `it passes the String to the correct translator`() {

        val mockCsvStringTranslator = mockCsvStringTranslator()

        val subject = cinqFieldCsvStringToFieldObject(
                cinqFieldCsvStringToFieldList = mockCsvStringTranslator
        )

        subject.translate("csv-string")

        verify(mockCsvStringTranslator).translate("csv-string")
    }

    @Test
    fun `it passes the FieldList to the correct translator`() {

        val mockFieldListTranslator = mockFieldListTranslator()

        val subject = cinqFieldCsvStringToFieldObject(
                cinqFieldCsvStringToFieldList = mockCsvStringTranslator(result = Success(listOf(listOf("A")))),
                fieldListToFieldObject = mockFieldListTranslator
        )

        subject.translate("csv-string")

        verify(mockFieldListTranslator).translate(listOf(listOf("A")))
    }

    @Test
    fun `it returns the translated FieldObject`() {

        val expectedFieldObject = FieldObject(name = "worked!")

        val subject = cinqFieldCsvStringToFieldObject(
                fieldListToFieldObject = mockFieldListTranslator(result = Success(expectedFieldObject))
        )

        subject.translate("csv-string") succeedsAndShouldReturn expectedFieldObject
    }

    @Test
    fun `it returns the failed string translation`() {

        val subject = cinqFieldCsvStringToFieldObject(
                cinqFieldCsvStringToFieldList = mockCsvStringTranslator(Failure("I failed"))
        )

        subject.translate("csv-string") failsWithMessage "I failed"
    }

    @Test
    fun `it returns the failed FieldList translation`() {

        val subject = cinqFieldCsvStringToFieldObject(
                fieldListToFieldObject = mockFieldListTranslator(Failure("I failed"))
        )

        subject.translate("csv-string") failsWithMessage "I failed"
    }

    private fun mockCsvStringTranslator(
            result: Result<String, ListOfFieldList> = Success(emptyList<FieldList>())
    ): Translator<String, ListOfFieldList> = mockTranslator(result)

    private fun mockFieldListTranslator(
            result: Result<String, FieldObject> = Success(FieldObject("default"))
    ): Translator<ListOfFieldList, FieldObject> = mockTranslator(result)

    private fun cinqFieldCsvStringToFieldObject(
            cinqFieldCsvStringToFieldList: Translator<String, ListOfFieldList> = mockCsvStringTranslator(),
            fieldListToFieldObject: Translator<ListOfFieldList, FieldObject> = mockFieldListTranslator()
    ) = CinqFieldCsvStringToFieldObject(
            cinqFieldCsvStringToFieldList = cinqFieldCsvStringToFieldList,
            fieldListToFieldObject = fieldListToFieldObject
    )
}