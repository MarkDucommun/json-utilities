package com.hcsc.de.claims.jsonReduction.cinqFieldCsv

import com.hcsc.de.claims.failsAnd
import com.hcsc.de.claims.succeedsAnd
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class CinqFieldCsvStringToFieldListTest {

    val subject = CinqFieldCsvStringToFieldList()

    @Test
    fun `it reads in the strange format from cinq fields csv string`() {

        val cinqFieldsCsvString = "A.B,C,Nonsense\nA.B,D,Nonsense\nA.B,E,Nonsense"

        subject.translate(cinqFieldsCsvString) succeedsAnd { fieldList ->

            assertThat(fieldList).containsExactly(
                    listOf("A", "B", "C"),
                    listOf("A", "B", "D"),
                    listOf("A", "B", "E")
            )
        }

    }

    @Test
    fun `it fails if string does not conform to the exact specification of the cinqfields csv`() {

        val cinqFieldsCsvString = "A.B,C\nA.B,D,Nonsense\nA.B,E,Nonsense"

        subject.translate(cinqFieldsCsvString) failsAnd  { message ->

            assertThat(message).isEqualTo("Some weird parsing error happened reading the Cinq Field CSV")
        }

    }
}