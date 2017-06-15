package com.hcsc.de.claims.math.helpers

import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class IntMathTest {

    @Test
    fun `can transform numbers - byte to byte`() {
        val start: Byte = 1
        val end: Byte = start.asType()
        assertThat(end).isEqualTo(1.toByte())
    }

    @Test
    fun `can transform numbers - byte to short`() {
        val start: Byte = 1
        val end: Short = start.asType()
        assertThat(end).isEqualTo(1.toShort())
    }

    @Test
    fun `can transform numbers - byte to int`() {
        val start: Byte = 1
        val end: Int = start.asType()
        assertThat(end).isEqualTo(1)
    }

    @Test
    fun `can transform numbers - byte to long`() {
        val start: Byte = 1
        val end: Long = start.asType()
        assertThat(end).isEqualTo(1.toLong())
    }

    @Test
    fun `can transform numbers - byte to float`() {
        val start: Byte = 1
        val end: Float = start.asType()
        assertThat(end).isEqualTo(1.toFloat())
    }

    @Test
    fun `can transform numbers - byte to double`() {
        val start: Byte = 1
        val end: Double = start.asType()
        assertThat(end).isEqualTo(1.toDouble())
    }

    // =================================================================================================================

    @Test
    fun `can transform numbers - short to byte`() {
        val start: Short = 1
        val end: Byte = start.asType()
        assertThat(end).isEqualTo(1.toByte())
    }

    @Test
    fun `can transform numbers - short to short`() {
        val start: Short = 1
        val end: Short = start.asType()
        assertThat(end).isEqualTo(1.toShort())
    }

    @Test
    fun `can transform numbers - short to int`() {
        val start: Short = 1
        val end: Int = start.asType()
        assertThat(end).isEqualTo(1)
    }

    @Test
    fun `can transform numbers - short to long`() {
        val start: Short = 1
        val end: Long = start.asType()
        assertThat(end).isEqualTo(1.toLong())
    }

    @Test
    fun `can transform numbers - short to float`() {
        val start: Short = 1
        val end: Float = start.asType()
        assertThat(end).isEqualTo(1.toFloat())
    }

    @Test
    fun `can transform numbers - short to double`() {
        val start: Short = 1
        val end: Double = start.asType()
        assertThat(end).isEqualTo(1.toDouble())
    }

    // =================================================================================================================

    @Test
    fun `can transform numbers - int to byte`() {
        val start: Int = 1
        val end: Byte = start.asType()
        assertThat(end).isEqualTo(1.toByte())
    }

    @Test
    fun `can transform numbers - int to short`() {
        val start: Int = 1
        val end: Short = start.asType()
        assertThat(end).isEqualTo(1.toShort())
    }

    @Test
    fun `can transform numbers - int to int`() {
        val start: Int = 1
        val end: Int = start.asType()
        assertThat(end).isEqualTo(1.toInt())
    }

    @Test
    fun `can transform numbers - int to long`() {
        val start: Int = 1
        val end: Long = start.asType()
        assertThat(end).isEqualTo(1.toLong())
    }

    @Test
    fun `can transform numbers - int to float`() {
        val start: Int = 1
        val end: Float = start.asType()
        assertThat(end).isEqualTo(1.toFloat())
    }

    @Test
    fun `can transform numbers - int to double`() {
        val start: Int = 1
        val end: Double = start.asType()
        assertThat(end).isEqualTo(1.toDouble())
    }

    // =================================================================================================================

    @Test
    fun `can transform numbers - long to byte`() {
        val start: Long = 1
        val end: Byte = start.asType()
        assertThat(end).isEqualTo(1.toByte())
    }

    @Test
    fun `can transform numbers - long to short`() {
        val start: Long = 1
        val end: Short = start.asType()
        assertThat(end).isEqualTo(1.toShort())
    }

    @Test
    fun `can transform numbers - long to int`() {
        val start: Long = 1
        val end: Int = start.asType()
        assertThat(end).isEqualTo(1.toInt())
    }

    @Test
    fun `can transform numbers - long to long`() {
        val start: Long = 1
        val end: Long = start.asType()
        assertThat(end).isEqualTo(1.toLong())
    }

    @Test
    fun `can transform numbers - long to float`() {
        val start: Long = 1
        val end: Float = start.asType()
        assertThat(end).isEqualTo(1.toFloat())
    }

    @Test
    fun `can transform numbers - long to double`() {
        val start: Long = 1
        val end: Double = start.asType()
        assertThat(end).isEqualTo(1.toDouble())
    }

    // =================================================================================================================

    @Test
    fun `can transform numbers - float to byte`() {
        val start: Float = 1.0F
        val end: Byte = start.asType()
        assertThat(end).isEqualTo(1.toByte())
    }

    @Test
    fun `can transform numbers - float to short`() {
        val start: Float = 1.0F
        val end: Short = start.asType()
        assertThat(end).isEqualTo(1.toShort())
    }

    @Test
    fun `can transform numbers - float to int`() {
        val start: Float = 1.0F
        val end: Int = start.asType()
        assertThat(end).isEqualTo(1.toInt())
    }

    @Test
    fun `can transform numbers - float to long`() {
        val start: Float = 1.0F
        val end: Long = start.asType()
        assertThat(end).isEqualTo(1.toLong())
    }

    @Test
    fun `can transform numbers - float to float`() {
        val start: Float = 1.0F
        val end: Float = start.asType()
        assertThat(end).isEqualTo(1.toFloat())
    }

    @Test
    fun `can transform numbers - float to double`() {
        val start: Float = 1.0F
        val end: Double = start.asType()
        assertThat(end).isEqualTo(1.toDouble())
    }

    // =================================================================================================================

    @Test
    fun `can transform numbers - double to byte`() {
        val start: Double = 1.0
        val end: Byte = start.asType()
        assertThat(end).isEqualTo(1.toByte())
    }

    @Test
    fun `can transform numbers - double to short`() {
        val start: Double = 1.0
        val end: Short = start.asType()
        assertThat(end).isEqualTo(1.toShort())
    }

    @Test
    fun `can transform numbers - double to int`() {
        val start: Double = 1.0
        val end: Int = start.asType()
        assertThat(end).isEqualTo(1.toInt())
    }

    @Test
    fun `can transform numbers - double to long`() {
        val start: Double = 1.0
        val end: Long = start.asType()
        assertThat(end).isEqualTo(1.toLong())
    }

    @Test
    fun `can transform numbers - double to float`() {
        val start: Double = 1.0
        val end: Float = start.asType()
        assertThat(end).isEqualTo(1.toFloat())
    }

    @Test
    fun `can transform numbers - double to double`() {
        val start: Double = 1.0
        val end: Double = start.asType()
        assertThat(end).isEqualTo(1.toDouble())
    }
}

