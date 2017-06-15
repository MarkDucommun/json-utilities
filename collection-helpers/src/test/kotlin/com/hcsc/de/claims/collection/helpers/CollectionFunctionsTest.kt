package com.hcsc.de.claims.collection.helpers

import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class CollectionFunctionsTest {

    val intList = listOf(1, 2, 3)
    val doubleList = listOf(1.0, 2.0, 3.0)

    @Test
    fun `compare`() {

        assertThat(intList.filterGreaterThanInt(1)).containsExactly(2, 3)
        assertThat(intList.filterGreaterThanOrEqualToInt(2)).containsExactly(2, 3)
        assertThat(intList.filterNotGreaterThanInt(1)).containsExactly(1)
        assertThat(intList.filterNotGreaterThanOrEqualToInt(2)).containsExactly(1)
        assertThat(intList.filterLessThanInt(3)).containsExactly(1, 2)
        assertThat(intList.filterLessThanOrEqualToInt(2)).containsExactly(1, 2)
        assertThat(intList.filterNotLessThanInt(2)).containsExactly(2, 3)
        assertThat(intList.filterNotLessThanOrEqualToInt(2)).containsExactly(3)

        assertThat(doubleList.filterGreaterThan(1.0)).containsExactly(2.0, 3.0)
        assertThat(doubleList.filterGreaterThanOrEqualTo(2.0)).containsExactly(2.0, 3.0)
        assertThat(doubleList.filterNotGreaterThan(1.0)).containsExactly(1.0)
        assertThat(doubleList.filterNotGreaterThanOrEqualTo(2.0)).containsExactly(1.0)
        assertThat(doubleList.filterLessThan(3.0)).containsExactly(1.0, 2.0)
        assertThat(doubleList.filterLessThanOrEqualTo(2.0)).containsExactly(1.0, 2.0)
        assertThat(doubleList.filterNotLessThan(2.0)).containsExactly(2.0, 3.0)
        assertThat(doubleList.filterNotLessThanOrEqualTo(2.0)).containsExactly(3.0)
    }
}