package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.distributions.generation.minimizedBinSizeBinDistribution
import com.hcsc.de.claims.results.succeedsAnd
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Ignore
import org.junit.Test

class ListToMinimizedBinSizeBinDistributionTest {

    @Test
    fun `it makes some buckets`() {

        List(5) { 1 }.minimizedBinSizeBinDistribution() succeedsAnd {
            assertThat(it.binCount).isEqualTo(1)
        }
    }

    @Test
    fun `two buckets`() {

        List(5) { 2 }.plus(List(5) { 3 }).minimizedBinSizeBinDistribution() succeedsAnd {

            assertThat(it.binCount).isEqualTo(2)
            assertThat(it.bins.first().members).isEqualTo(List(5) { 2 })
            assertThat(it.bins.last().members).isEqualTo(List(5) { 3 })
        }
    }

    @Test
    fun `three buckets`() {

        List(5) { 2 }.plus(List(5) { 3 }).plus(List(5) { 4 }).minimizedBinSizeBinDistribution() succeedsAnd {

            assertThat(it.binCount).isEqualTo(3)
            assertThat(it.bins.first().members).isEqualTo(List(5) { 2 })
            assertThat(it.bins[1].members).isEqualTo(List(5) { 3 })
            assertThat(it.bins.last().members).isEqualTo(List(5) { 4 })
        }
    }
}
