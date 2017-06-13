package com.hcsc.de.claims.jsonReducing

import com.hcsc.de.claims.Translator
import com.hcsc.de.claims.jsonReduction.FieldObject
import com.hcsc.de.claims.mockTranslator
import com.hcsc.de.claims.results.*
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class CinqFieldCsvStringToFieldObjectTest {

    @org.junit.Test
    fun `it passes the String to the correct translator`() {

        val mockCsvStringTranslator = mockCsvStringTranslator()

        val subject = cinqFieldCsvStringToFieldObject(
                cinqFieldCsvStringToFieldList = mockCsvStringTranslator
        )

        subject.translate("csv-string")

        com.nhaarman.mockito_kotlin.verify(mockCsvStringTranslator).translate("csv-string")
    }

    @org.junit.Test
    fun `it passes the FieldList to the correct translator`() {

        val mockFieldListTranslator = mockFieldListTranslator()

        val subject = cinqFieldCsvStringToFieldObject(
                cinqFieldCsvStringToFieldList = mockCsvStringTranslator(result = com.hcsc.de.claims.results.Success(listOf(listOf("A")))),
                fieldListToFieldObject = mockFieldListTranslator
        )

        subject.translate("csv-string")

        com.nhaarman.mockito_kotlin.verify(mockFieldListTranslator).translate(listOf(listOf("A")))
    }

    @org.junit.Test
    fun `it returns the translated FieldObject`() {

        val expectedFieldObject = FieldObject(name = "worked!")

        val subject = cinqFieldCsvStringToFieldObject(
                fieldListToFieldObject = mockFieldListTranslator(result = com.hcsc.de.claims.results.Success(expectedFieldObject))
        )

        subject.translate("csv-string") succeedsAndShouldReturn expectedFieldObject
    }

    @org.junit.Test
    fun `it returns the failed string translation`() {

        val subject = cinqFieldCsvStringToFieldObject(
                cinqFieldCsvStringToFieldList = mockCsvStringTranslator(com.hcsc.de.claims.results.Failure("I failed"))
        )

        subject.translate("csv-string") failsWithMessage "I failed"
    }

    @org.junit.Test
    fun `it returns the failed FieldList translation`() {

        val subject = cinqFieldCsvStringToFieldObject(
                fieldListToFieldObject = mockFieldListTranslator(com.hcsc.de.claims.results.Failure("I failed"))
        )

        subject.translate("csv-string") failsWithMessage "I failed"
    }

    private fun mockCsvStringTranslator(
            result: com.hcsc.de.claims.results.Result<String, ListOfFieldList> = com.hcsc.de.claims.results.Success(emptyList<FieldList>())
    ): Translator<String, ListOfFieldList> = mockTranslator(result)

    private fun mockFieldListTranslator(
            result: com.hcsc.de.claims.results.Result<String, FieldObject> = com.hcsc.de.claims.results.Success(FieldObject("default"))
    ): Translator<ListOfFieldList, FieldObject> = mockTranslator(result)

    private fun cinqFieldCsvStringToFieldObject(
            cinqFieldCsvStringToFieldList: Translator<String, ListOfFieldList> = mockCsvStringTranslator(),
            fieldListToFieldObject: Translator<ListOfFieldList, FieldObject> = mockFieldListTranslator()
    ) = CinqFieldCsvStringToFieldObject(
            cinqFieldCsvStringToFieldList = cinqFieldCsvStringToFieldList,
            fieldListToFieldObject = fieldListToFieldObject
    )
}