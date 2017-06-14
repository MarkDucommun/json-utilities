package com.hcsc.de.claims.distributions

import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Ignore
import org.junit.Test

class unknownVariableBinWidthDistributionTest {

    @Test
    fun `it makes some buckets`() {

        val listOfOnes = List(5) { 1 }.unknownVariableBinWidthDistribution()

        assertThat(listOfOnes.numberOfBins).isEqualTo(1)
    }

    @Test
    @Ignore("FIX THIS!")
    fun `two buckets`() {

        val dist = List(5) { 2 }.plus(List(5) { 3 }).unknownVariableBinWidthDistribution()

        assertThat(dist.numberOfBins).isEqualTo(2)
        assertThat(dist.bins.first().members).isEqualTo(List(5) { 2 })
        assertThat(dist.bins.last().members).isEqualTo(List(5) { 3 })
    }

    @Test
    @Ignore("FIX THIS!")
    fun `three buckets`() {

        val dist = List(5) { 2 }.plus(List(5) { 3 }).plus(List(5) { 4 }).unknownVariableBinWidthDistribution()

        assertThat(dist.numberOfBins).isEqualTo(3)
        assertThat(dist.bins.first().members).isEqualTo(List(5) { 2 })
        assertThat(dist.bins[1].members).isEqualTo(List(5) { 3 })
        assertThat(dist.bins.last().members).isEqualTo(List(5) { 4 })
    }
}
