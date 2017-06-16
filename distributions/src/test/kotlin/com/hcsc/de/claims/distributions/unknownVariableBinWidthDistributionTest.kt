package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.distributions.generation.minimizedBinSizeBinDistribution
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Ignore
import org.junit.Test

class unknownVariableBinWidthDistributionTest {

    @Test
    fun `it makes some buckets`() {

        val listOfOnes = List(5) { 1 }.minimizedBinSizeBinDistribution()

        assertThat(listOfOnes.binCount).isEqualTo(1)
    }

    @Test
    fun `two buckets`() {

        val dist = List(5) { 2 }.plus(List(5) { 3 }).minimizedBinSizeBinDistribution()

        assertThat(dist.binCount).isEqualTo(2)
        assertThat(dist.bins.first().members).isEqualTo(List(5) { 2 })
        assertThat(dist.bins.last().members).isEqualTo(List(5) { 3 })
    }

    @Test
    fun `three buckets`() {

        val dist = List(5) { 2 }.plus(List(5) { 3 }).plus(List(5) { 4 }).minimizedBinSizeBinDistribution()

        assertThat(dist.binCount).isEqualTo(3)
        assertThat(dist.bins.first().members).isEqualTo(List(5) { 2 })
        assertThat(dist.bins[1].members).isEqualTo(List(5) { 3 })
        assertThat(dist.bins.last().members).isEqualTo(List(5) { 4 })
    }
}
