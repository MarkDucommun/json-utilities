package com.hcsc.de.claims.collection.helpers

import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class NonEmptyNumberListFunctionsTest {

    @Test
    fun average() {

        assertThat(nonEmptyListOf(1, 1, 1).average()).isEqualTo(1)

        assertThat(nonEmptyListOf(1, 3, 3, 3, 4).average()).isEqualTo(3)

        assertThat(nonEmptyListOf(1.0, 3.0, 3.0, 3.0, 4.0).average()).isEqualTo(2.8)
    }

    @Test
    fun minimum() {

        assertThat(nonEmptyListOf(1, 2).minimum()).isEqualTo(1)

        assertThat(nonEmptyListOf(1L, 2L).minimum()).isEqualTo(1L)

        assertThat(nonEmptyListOf(1.0, 2.0).minimum()).isEqualTo(1.0)
    }

    @Test
    fun maximum() {

        assertThat(nonEmptyListOf(1, 2).maximum()).isEqualTo(2)

        assertThat(nonEmptyListOf(1L, 2L).maximum()).isEqualTo(2L)

        assertThat(nonEmptyListOf(1.0, 2.0).maximum()).isEqualTo(2.0)
    }

    @Test
    fun median() {

        assertThat(nonEmptyListOf(2, 1, 2).median()).isEqualTo(2)
        assertThat(nonEmptyListOf(2, 1).median()).isEqualTo(2)
        assertThat(nonEmptyListOf(2, 1).median()).isEqualTo(2)

        assertThat(nonEmptyListOf(2L, 1L, 2L).median()).isEqualTo(2L)
        assertThat(nonEmptyListOf(2L, 1L).median()).isEqualTo(2L)
        assertThat(nonEmptyListOf(2L).median()).isEqualTo(2L)

        assertThat(nonEmptyListOf(2.0, 1.0, 2.0).median()).isEqualTo(2.0)
        assertThat(nonEmptyListOf(2.0, 1.0).median()).isEqualTo(1.5)
        assertThat(nonEmptyListOf(2.0).median()).isEqualTo(2.0)
    }

    @Test
    fun simpleMode() {

        assertThat(nonEmptyListOf(1, 1, 2).simpleMode()).isEqualTo(1)
        assertThat(nonEmptyListOf(1, 1, 2, 2).simpleMode()).isEqualTo(2)

        assertThat(nonEmptyListOf(1L, 1L, 2L).simpleMode()).isEqualTo(1L)
        assertThat(nonEmptyListOf(1L, 1L, 2L, 2L).simpleMode()).isEqualTo(2L)

        assertThat(nonEmptyListOf(1.0, 1.0, 2.0).simpleMode()).isEqualTo(1.0)
        assertThat(nonEmptyListOf(1.0, 1.0, 2.0, 2.0).simpleMode()).isEqualTo(2.0)
    }
}