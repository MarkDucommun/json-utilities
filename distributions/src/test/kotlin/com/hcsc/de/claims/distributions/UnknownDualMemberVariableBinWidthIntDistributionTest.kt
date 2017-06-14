package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.distributions.generation.unknownDualMemberVariableBinWidthDistribution
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Ignore
import org.junit.Test

class UnknownDualMemberVariableBinWidthIntDistributionTest {

    @Test
    fun `it makes some buckets`() {

        val listOfOnes = List(5) { 1 }

        val dist = DistributionPair(one = listOfOnes, two = listOfOnes).unknownDualMemberVariableBinWidthDistribution()

        assertThat(dist.bins.size).isEqualTo(1)
        assertThat(dist.bins.first().binOne.size).isEqualTo(5)
        assertThat(dist.bins.first().binTwo.size).isEqualTo(5)
    }

    @Test
    fun `one bucket, bucket range of two`() {

        val listOfOnes = List(5) { 1 }
        val listOfTwos = List(5) { 2 }

        val dist = DistributionPair(one = listOfOnes, two = listOfTwos).unknownDualMemberVariableBinWidthDistribution()

        assertThat(dist.bins.size).isEqualTo(1)
        assertThat(dist.bins.first().binOne.size).isEqualTo(5)
        assertThat(dist.bins.first().binTwo.size).isEqualTo(5)
    }

    @Test
    @Ignore("FIX THIS!")
    fun `two buckets, bucket range of two`() {

        val listOfOnes = List(5) { 1 }.plus(List(5) { 3 })
        val listOfTwos = List(10) { 2 }.plus(List(5) { 7 })

        val dist = DistributionPair(one = listOfOnes, two = listOfTwos).unknownDualMemberVariableBinWidthDistribution()

        assertThat(dist.bins.size).isEqualTo(2)
        assertThat(dist.bins.first().binOne.size).isEqualTo(5)
        assertThat(dist.bins.first().binTwo.size).isEqualTo(10)
        assertThat(dist.bins.last().binOne.size).isEqualTo(5)
        assertThat(dist.bins.last().binTwo.size).isEqualTo(5)
    }

    @Test
    @Ignore("FIX THIS!")
    fun `can be split into two normal UnknownVariableBinWidthDistributions`() {

        val listOfOnes = List(5) { 1 }.plus(List(5) { 3 })
        val listOfTwos = List(10) { 2 }.plus(List(5) { 7 })

        val dist = DistributionPair(one = listOfOnes, two = listOfTwos).unknownDualMemberVariableBinWidthDistribution()

        val (distOne, distTwo) = dist.asTwoDistributions

        assertThat(distOne.bins.size).isEqualTo(2)
        assertThat(distTwo.bins.size).isEqualTo(2)
        assertThat(distOne.bins.first().size).isEqualTo(5)
        assertThat(distTwo.bins.first().size).isEqualTo(10)
        assertThat(distOne.bins.last().size).isEqualTo(5)
        assertThat(distTwo.bins.last().size).isEqualTo(5)
    }
}