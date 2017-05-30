package com.hcsc.de.claims.distributions

import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class UnknownDualMemberVariableBinWidthIntDistributionTest {

    @Test
    fun `it makes some buckets`() {

        val listOfOnes = List(5) { 1 }

        val dist = DistributionPair(one = listOfOnes, two = listOfOnes).unknownDualMemberVariableBinWidthDistribution()

        assertThat(dist.numberOfBins).isEqualTo(1)
        assertThat(dist.bins.first().memberOneCount).isEqualTo(5)
        assertThat(dist.bins.first().memberTwoCount).isEqualTo(5)
    }

    @Test
    fun `one bucket, bucket range of two`() {

        val listOfOnes = List(5) { 1 }
        val listOfTwos = List(5) { 2 }

        val dist = DistributionPair(one = listOfOnes, two = listOfTwos).unknownDualMemberVariableBinWidthDistribution()

        assertThat(dist.numberOfBins).isEqualTo(1)
        assertThat(dist.bins.first().memberOneCount).isEqualTo(5)
        assertThat(dist.bins.first().memberTwoCount).isEqualTo(5)
    }

    @Test
    fun `two buckets, bucket range of two`() {

        val listOfOnes = List(5) { 1 }.plus(List(5) { 3 })
        val listOfTwos = List(10) { 2 }.plus(List(5) { 7 })

        val dist = DistributionPair(one = listOfOnes, two = listOfTwos).unknownDualMemberVariableBinWidthDistribution()

        assertThat(dist.numberOfBins).isEqualTo(2)
        assertThat(dist.bins.first().memberOneCount).isEqualTo(5)
        assertThat(dist.bins.first().memberTwoCount).isEqualTo(10)
        assertThat(dist.bins.last().memberOneCount).isEqualTo(5)
        assertThat(dist.bins.last().memberTwoCount).isEqualTo(5)
    }

    @Test
    fun `can be split into two normal UnknownVariableBinWidthDistributions`() {

        val listOfOnes = List(5) { 1 }.plus(List(5) { 3 })
        val listOfTwos = List(10) { 2 }.plus(List(5) { 7 })

        val dist = DistributionPair(one = listOfOnes, two = listOfTwos).unknownDualMemberVariableBinWidthDistribution()

        val (distOne, distTwo) = dist.asTwoDistributions

        assertThat(distOne.numberOfBins).isEqualTo(2)
        assertThat(distTwo.numberOfBins).isEqualTo(2)
        assertThat(distOne.bins.first().count).isEqualTo(5)
        assertThat(distTwo.bins.first().count).isEqualTo(10)
        assertThat(distOne.bins.last().count).isEqualTo(5)
        assertThat(distTwo.bins.last().count).isEqualTo(5)
    }
}