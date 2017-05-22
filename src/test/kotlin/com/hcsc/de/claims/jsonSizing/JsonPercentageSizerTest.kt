package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.succeedsAndShouldReturn
import org.junit.Test

class JsonPercentageSizerTest {

    val percentageSizer = JsonPercentageSizer()

    @Test
    fun `it creates a percentage representation for a Leaf Node`() {

        percentageSizer.generatePercentage(JsonSizeLeafOverview(
                name = "test",
                size = Distribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        standardDeviation = 0.0
                )
        )) succeedsAndShouldReturn JsonPercentageSizeLeaf(
                name = "test",
                localPercent = Distribution(
                        average = 100,
                        minimum = 100,
                        maximum = 100,
                        standardDeviation = 0.0
                ),
                globalPercent = Distribution(
                        average = 100,
                        minimum = 100,
                        maximum = 100,
                        standardDeviation = 0.0
                )
        )
    }
}