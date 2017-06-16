package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.binDistributions.GenericFixedWidthBinDistribution
import com.hcsc.de.claims.distributions.bins.SimpleBin
import com.hcsc.de.claims.results.succeedsAndShouldReturn
import org.junit.Test

class FixedWidthBinDistributionGeneratorTest {

    val subject = FixedWidthBinDistributionGenerator(toType = Double::toInt)

    @Test
    fun `it creates a simple fixed distribution`() {

        subject.create(DistributionRequest.FixedWidthBinDistributionRequest(
                list = listOf(1, 2, 3),
                binWidth = 1
        )) succeedsAndShouldReturn DistributionProfile(
                distribution = GenericFixedWidthBinDistribution(
                        bins = listOf(
                                SimpleBin(identifyingCharacteristic = 1, size = 1),
                                SimpleBin(identifyingCharacteristic = 2, size = 1),
                                SimpleBin(identifyingCharacteristic = 3, size = 1)
                        ),
                        binWidth = 1,
                        average = 2,
                        minimum = 1,
                        maximum = 3,
                        median = 2,
                        mode = 3
                ),
                pValue = 1.0
        )
    }
}