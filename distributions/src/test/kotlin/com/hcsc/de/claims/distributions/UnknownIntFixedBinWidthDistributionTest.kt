package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.distributions.binDistributions.AutomaticFixedWidthBinDistribution
import com.hcsc.de.claims.distributions.bins.SimpleBin
import com.hcsc.de.claims.distributions.generation.unknownDistribution
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class UnknownIntFixedBinWidthDistributionTest {

    @Test
    fun `it creates a simple UnknownDistribution from a list of binary integers`() {

        val distribution = listOf(1, 1, 1, 0, 0).unknownDistribution(numberOfBins = 1000)

        assertThat(distribution).isEqualTo(AutomaticFixedWidthBinDistribution(
                average = 1,
                minimum = 0,
                maximum = 1,
                mode = 1,
                median = 1,
                binWidth = 1,
                rawBins = listOf(
                        SimpleBin(identifyingCharacteristic = 0, size = 2),
                        SimpleBin(identifyingCharacteristic = 1, size = 3)
                )
        ))
    }

    @Test
    fun `it creates a simple UnknownDistribution from a list of integers with nu`() {

        val distribution = listOf(1, 1, 1, 2, 2, 3, 4, 5, 5).unknownDistribution(numberOfBins = 5)

        assertThat(distribution).isEqualTo(AutomaticFixedWidthBinDistribution(
                average = 3,
                minimum = 1,
                maximum = 5,
                mode = 1,
                median = 2,
                binWidth = 1,
                rawBins = listOf(
                        SimpleBin(identifyingCharacteristic = 1, size = 3),
                        SimpleBin(identifyingCharacteristic = 2, size = 2),
                        SimpleBin(identifyingCharacteristic = 3, size = 1),
                        SimpleBin(identifyingCharacteristic = 4, size = 1),
                        SimpleBin(identifyingCharacteristic = 5, size = 2)
                )
        ))
    }

    @Test
    fun `it creates a simple UnknownDistribution from a list of integers`() {

        val distribution = listOf(1, 1, 1, 2, 2, 3, 4, 5, 5, 6).unknownDistribution(numberOfBins = 3)

        assertThat(distribution).isEqualTo(AutomaticFixedWidthBinDistribution(
                average = 3,
                minimum = 1,
                maximum = 6,
                mode = 1,
                median = 2,
                binWidth = 2,
                rawBins = listOf(
                        SimpleBin(identifyingCharacteristic = 1, size = 5),
                        SimpleBin(identifyingCharacteristic = 3, size = 2),
                        SimpleBin(identifyingCharacteristic = 5, size = 3)
                )
        ))
    }

    @Test // TODO
    fun `it creates a simple UnknownDistribution from a list of integers blah`() {

        val distribution = listOf(1, 1, 1, 2, 2, 3, 4, 5, 5, 6).unknownDistribution(numberOfBins = 2)

        assertThat(distribution).isEqualTo(AutomaticFixedWidthBinDistribution(
                average = 3,
                minimum = 1,
                maximum = 6,
                mode = 1,
                median = 2,
                binWidth = 3,
                rawBins = listOf(
                        SimpleBin(identifyingCharacteristic = 1, size = 6),
                        SimpleBin(identifyingCharacteristic = 4, size = 4)
                )
        ))
    }

    @Test
    fun `it can create a UnknownDistribution for a specified min and max from a list of integers`() {

        val distribution = listOf(1, 1, 1, 2, 2, 3, 4, 5, 5, 6).unknownDistribution(
                numberOfBins = 3,
                rangeMinimum = 0,
                rangeMaximum = 8
        )

        assertThat(distribution).isEqualTo(AutomaticFixedWidthBinDistribution(
                average = 3,
                minimum = 1,
                maximum = 6,
                mode = 1,
                median = 2,
                binWidth = 3,
                rawBins = listOf(
                        SimpleBin(identifyingCharacteristic = 0, size = 5),
                        SimpleBin(identifyingCharacteristic = 3, size = 4),
                        SimpleBin(identifyingCharacteristic = 6, size = 1)
                )
        ))
    }

    @Test
    fun `it excludes values from the distribution that are not within the specified range`() {

        val distribution = listOf(1, 1, 1, 2, 2, 3, 4, 5, 5, 6).unknownDistribution(
                numberOfBins = 2,
                rangeMinimum = 2,
                rangeMaximum = 5
        )

        assertThat(distribution).isEqualTo(AutomaticFixedWidthBinDistribution(
                average = 3,
                minimum = 2,
                maximum = 5,
                mode = 1,
                median = 2,
                binWidth = 2,
                rawBins = listOf(
                        SimpleBin(identifyingCharacteristic = 2, size = 3),
                        SimpleBin(identifyingCharacteristic = 4, size = 3)
                )
        ))
    }
}