package com.hcsc.de.claims.jsonSizing

import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class unknownDistributionTest {

    @Test
    fun `it creates a simple UnknownDistribution from a list of binary integers`() {

        val distribution = listOf(1, 1, 1, 0, 0).unknownDistribution(numberOfBins = 1000)

        assertThat(distribution).isEqualTo(UnknownIntFixedBinWidthDistribution(
                average = 1,
                minimum = 0,
                maximum = 1,
                mode = 1,
                median = 1,
                numberOfBins = 2,
                sizeOfBin = 1,
                bins = listOf(
                        FixedWidthBin(startValue = 0, count = 2),
                        FixedWidthBin(startValue = 1, count = 3)
                )
        ))
    }

    @Test
    fun `it creates a simple UnknownDistribution from a list of integers with nu`() {

        val distribution = listOf(1, 1, 1, 2, 2, 3, 4, 5, 5).unknownDistribution(numberOfBins = 5)

        assertThat(distribution).isEqualTo(UnknownIntFixedBinWidthDistribution(
                average = 3,
                minimum = 1,
                maximum = 5,
                mode = 1,
                median = 2,
                numberOfBins = 5,
                sizeOfBin = 1,
                bins = listOf(
                        FixedWidthBin(startValue = 1, count = 3),
                        FixedWidthBin(startValue = 2, count = 2),
                        FixedWidthBin(startValue = 3, count = 1),
                        FixedWidthBin(startValue = 4, count = 1),
                        FixedWidthBin(startValue = 5, count = 2)
                )
        ))
    }

    @Test
    fun `it creates a simple UnknownDistribution from a list of integers`() {

        val distribution = listOf(1, 1, 1, 2, 2, 3, 4, 5, 5, 6).unknownDistribution(numberOfBins = 3)

        assertThat(distribution).isEqualTo(UnknownIntFixedBinWidthDistribution(
                average = 3,
                minimum = 1,
                maximum = 6,
                mode = 1,
                median = 2,
                numberOfBins = 3,
                sizeOfBin = 2,
                bins = listOf(
                        FixedWidthBin(startValue = 1, count = 5),
                        FixedWidthBin(startValue = 3, count = 2),
                        FixedWidthBin(startValue = 5, count = 3)
                )
        ))
    }

    @Test // TODO
    fun `it creates a simple UnknownDistribution from a list of integers blah`() {

        val distribution = listOf(1, 1, 1, 2, 2, 3, 4, 5, 5, 6).unknownDistribution(numberOfBins = 2)

        assertThat(distribution).isEqualTo(UnknownIntFixedBinWidthDistribution(
                average = 3,
                minimum = 1,
                maximum = 6,
                mode = 1,
                median = 2,
                numberOfBins = 2,
                sizeOfBin = 3,
                bins = listOf(
                        FixedWidthBin(startValue = 1, count = 6),
                        FixedWidthBin(startValue = 4, count = 4)
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

        assertThat(distribution).isEqualTo(UnknownIntFixedBinWidthDistribution(
                average = 3,
                minimum = 1,
                maximum = 6,
                mode = 1,
                median = 2,
                numberOfBins = 3,
                sizeOfBin = 3,
                bins = listOf(
                        FixedWidthBin(startValue = 0, count = 5),
                        FixedWidthBin(startValue = 3, count = 4),
                        FixedWidthBin(startValue = 6, count = 1)
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

        assertThat(distribution).isEqualTo(UnknownIntFixedBinWidthDistribution(
                average = 3,
                minimum = 2,
                maximum = 5,
                mode = 1,
                median = 2,
                numberOfBins = 2,
                sizeOfBin = 2,
                bins = listOf(
                        FixedWidthBin(startValue = 2, count = 3),
                        FixedWidthBin(startValue = 4, count = 3)
                )
        ))
    }
}