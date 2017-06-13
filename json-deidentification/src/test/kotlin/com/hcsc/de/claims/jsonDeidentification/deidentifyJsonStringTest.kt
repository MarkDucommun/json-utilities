package com.hcsc.de.claims.jsonDeIdentifier

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class DeidentifyJsonString {

    @Test
    fun `it can de-identify a very simple JSON object`() {

        val identifiedString = mapOf("A" to "1").writeAsString()

        val expectedDeidentifiedString = mapOf("A" to "?").writeAsString()

        val deidentifiedString = identifiedString.deidentify()

        assertThat(deidentifiedString).isEqualTo(expectedDeidentifiedString)
    }

    @Test
    fun `it can de-identify objects with null`() {

        val identifiedString = mapOf("A" to null).writeAsString()

        val expectedDeidentifiedString = mapOf("A" to null).writeAsString()

        val deidentifiedString = identifiedString.deidentify()

        assertThat(deidentifiedString).isEqualTo(expectedDeidentifiedString)
    }

    @Test
    fun `it can de-identify slightly more complex objects`() {

        val identifiedString = mapOf("A" to mapOf("B" to "realData")).writeAsString()

        val expectedDeidentifiedString = mapOf("A" to mapOf("B" to "????????")).writeAsString()

        val deidentifiedString = identifiedString.deidentify()

        assertThat(deidentifiedString).isEqualTo(expectedDeidentifiedString)
    }

    @Test
    fun `it can de-identify arrays`() {

        val identifiedString = mapOf("A" to listOf("B", "C")).writeAsString()

        val expectedDeidentifiedString = mapOf("A" to listOf("?", "?")).writeAsString()

        val deidentifiedString = identifiedString.deidentify()

        assertThat(deidentifiedString).isEqualTo(expectedDeidentifiedString)
    }

    @Test
    fun `it can de-identify arrays with complex objects`() {

        val identifiedString = mapOf("A" to listOf(mapOf("B" to "1"))).writeAsString()

        val expectedDeidentifiedString = mapOf("A" to listOf(mapOf("B" to "?"))).writeAsString()

        val deidentifiedString = identifiedString.deidentify()

        assertThat(deidentifiedString).isEqualTo(expectedDeidentifiedString)
    }

    @Test
    fun `it can de-identify arrays with nulls`() {

        val identifiedString = mapOf("A" to listOf(null)).writeAsString()

        val expectedDeidentifiedString = mapOf("A" to listOf(null)).writeAsString()

        val deidentifiedString = identifiedString.deidentify()

        assertThat(deidentifiedString).isEqualTo(expectedDeidentifiedString)
    }

    @Test
    fun `it can de-identify arrays with arrays`() {

        val identifiedString = mapOf("A" to listOf(listOf("A"))).writeAsString()

        val expectedDeidentifiedString = mapOf("A" to listOf(listOf("?"))).writeAsString()

        val deidentifiedString = identifiedString.deidentify()

        assertThat(deidentifiedString).isEqualTo(expectedDeidentifiedString)
    }

    val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

    val subject = JsonDeIdentifier(objectMapper = objectMapper)

    private fun JsonString.deidentify() = subject.deidentifyJson(this)

    private fun Any.writeAsString() = objectMapper.writeValueAsString(this)
}