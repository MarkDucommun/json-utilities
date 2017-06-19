package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.distributions.binDistributions.GenericFixedWidthBinDistribution
import com.hcsc.de.claims.distributions.bins.SimpleBin
import com.hcsc.de.claims.distributions.generation.DistributionProfile
import com.hcsc.de.claims.distributions.generation.DistributionRequest
import com.hcsc.de.claims.distributions.generation.FixedWidthBinCountDistributionGenerator
import com.hcsc.de.claims.distributions.generation.toFixedWidthBinCountDistribution
import com.hcsc.de.claims.results.succeedsAndShouldReturn
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class FixedWidthBinCountDistributionGeneratorTest {

    val subject = FixedWidthBinCountDistributionGenerator<Int>(toType = { Math.round(this).toInt() })

    @Test
    fun `it creates a simple distribution without any empty buckets`() {

        subject.create(DistributionRequest.FixedWidthBinCountDistributionRequest(
                list = listOf(1, 1, 1, 0, 0),
                binCount = 1000
        )) succeedsAndShouldReturn DistributionProfile(
                pValue = 1.0,
                distribution = GenericFixedWidthBinDistribution(
                        average = 1,
                        minimum = 0,
                        maximum = 1,
                        mode = 1,
                        median = 1,
                        binWidth = 0,
                        bins = listOf(
                                SimpleBin(identifyingCharacteristic = 0, size = 2),
                                SimpleBin(identifyingCharacteristic = 1, size = 3)
                        )
                )
        )
    }

    @Test
    fun `it distributes integers correctly`() {

        subject.create(DistributionRequest.FixedWidthBinCountDistributionRequest(
                list = listOf(1, 1, 1, 2, 2, 3, 4, 5, 5),
                binCount = 5
        )) succeedsAndShouldReturn DistributionProfile(
                pValue = 1.0,
                distribution = GenericFixedWidthBinDistribution(
                        average = 3,
                        minimum = 1,
                        maximum = 5,
                        mode = 1,
                        median = 2,
                        binWidth = 1,
                        bins = listOf(
                                SimpleBin(identifyingCharacteristic = 1, size = 3),
                                SimpleBin(identifyingCharacteristic = 2, size = 2),
                                SimpleBin(identifyingCharacteristic = 3, size = 1),
                                SimpleBin(identifyingCharacteristic = 4, size = 1),
                                SimpleBin(identifyingCharacteristic = 5, size = 2)
                        )
                )
        )
    }

    @Test
    fun `it creates multiple integer width buckets`() {

        subject.create(DistributionRequest.FixedWidthBinCountDistributionRequest(
                list = listOf(1, 1, 1, 2, 2, 3, 4, 5, 5, 6),
                binCount = 2
        )) succeedsAndShouldReturn DistributionProfile(
                pValue = 1.0,
                distribution = GenericFixedWidthBinDistribution(
                        average = 3,
                        minimum = 1,
                        maximum = 6,
                        mode = 1,
                        median = 3,
                        binWidth = 3,
                        bins = listOf(
                                SimpleBin(identifyingCharacteristic = 1, size = 6),
                                SimpleBin(identifyingCharacteristic = 4, size = 4)
                        )
                )
        )
    }

    @Test
    fun `it can create a distribution with multiple integer width buckets`() {

        subject.create(DistributionRequest.FixedWidthBinCountDistributionRequest(
                list = listOf(1, 1, 1, 2, 2, 3, 4, 5, 5, 6),
                binCount = 3
        )) succeedsAndShouldReturn DistributionProfile(
                pValue = 1.0,
                distribution = GenericFixedWidthBinDistribution(
                        average = 3,
                        minimum = 1,
                        maximum = 6,
                        mode = 1,
                        median = 3,
                        binWidth = 2,
                        bins = listOf(
                                SimpleBin(identifyingCharacteristic = 1, size = 5),
                                SimpleBin(identifyingCharacteristic = 3, size = 2),
                                SimpleBin(identifyingCharacteristic = 5, size = 3)
                        )
                )
        )
    }

    @Test
    fun `it excludes values from the distribution that are not within the specified range`() {

        subject.create(DistributionRequest.FixedWidthBinCountDistributionRequest(
                list = listOf(1, 1, 1, 2, 2, 3, 4, 5, 5, 6),
                binCount = 2,
                minimum = 2,
                maximum = 5
        )) succeedsAndShouldReturn DistributionProfile(
                pValue = 1.0,
                distribution = GenericFixedWidthBinDistribution(
                        average = 4,
                        minimum = 2,
                        maximum = 5,
                        mode = 5,
                        median = 4,
                        binWidth = 2,
                        bins = listOf(
                                SimpleBin(identifyingCharacteristic = 2, size = 3),
                                SimpleBin(identifyingCharacteristic = 4, size = 3)
                        )
                )
        )
    }
}