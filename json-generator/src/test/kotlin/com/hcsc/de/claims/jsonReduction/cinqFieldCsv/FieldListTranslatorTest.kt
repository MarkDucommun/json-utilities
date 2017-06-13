package com.hcsc.de.claims.jsonReduction.cinqFieldCsv

import com.hcsc.de.claims.jsonReduction.FieldObject
import com.hcsc.de.claims.results.Success
import com.hcsc.de.claims.results.succeedsAnd
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class FieldListTranslatorTest {

    private val subject = FieldListTranslator()

    @Test
    fun `translate should not fail`() {

        val fieldList: ListOfFieldList = listOf(listOf("a", "b"))

        val result = subject.translate(fieldList)

        assertThat(result).isInstanceOf(Success::class.java)
    }

    @Test
    fun `translate should translate a single layer Field List`() {

        val fieldList: ListOfFieldList = listOf(listOf("A", "1"), listOf("A", "2"), listOf("B", "3"))

        subject.translate(fieldList) succeedsAnd { (name, properties) ->

            assertThat(name).isEqualTo("root")
            assertThat(properties).containsExactly(
                    FieldObject(
                            name = "A",
                            properties = listOf(
                                    FieldObject(name = "1"),
                                    FieldObject(name = "2")
                            )
                    ),
                    FieldObject(
                            name = "B",
                            properties = listOf(FieldObject(name = "3"))
                    ))
        }
    }

    @Test
    fun `translate should translate a deeply layered Field List`() {

        val fieldList: ListOfFieldList = listOf(
                listOf("A", "1", "-"),
                listOf("A", "1", "+"),
                listOf("A", "2")
        )

        subject.translate(fieldList) succeedsAnd { (name, properties) ->

            assertThat(name).isEqualTo("root")
            assertThat(properties).containsExactly(
                    FieldObject(
                            name = "A",
                            properties = listOf(
                                    FieldObject(name = "1", properties = listOf(
                                            FieldObject(name = "-"),
                                            FieldObject(name = "+")
                                    )),
                                    FieldObject(name = "2")
                            )
                    ))
        }
    }
}