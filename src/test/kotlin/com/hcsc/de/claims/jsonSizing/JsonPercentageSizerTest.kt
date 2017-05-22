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
                localPercent = PercentageDistribution(
                        average = 100.0,
                        minimum = 100.0,
                        maximum = 100.0
                ),
                globalPercent = PercentageDistribution(
                        average = 100.0,
                        minimum = 100.0,
                        maximum = 100.0
                )
        )
    }

    @Test
    fun `it creates a percentage representation for an Object`() {

        val input = JsonSizeObjectOverview(
                name = "test",
                size = Distribution(
                        average = 100,
                        minimum = 100,
                        maximum = 100,
                        standardDeviation = 0.0
                ),
                children = listOf(
                        JsonSizeLeafOverview(
                                name = "A",
                                size = Distribution(
                                        average = 70,
                                        minimum = 70,
                                        maximum = 70,
                                        standardDeviation = 0.0
                                )
                        ),
                        JsonSizeLeafOverview(
                                name = "B",
                                size = Distribution(
                                        average = 30,
                                        minimum = 30,
                                        maximum = 30,
                                        standardDeviation = 0.0
                                )
                        )
                )
        )

        percentageSizer.generatePercentage(input) succeedsAndShouldReturn JsonPercentageSizeObject(
                name = "test",
                localPercent = PercentageDistribution(
                        average = 100.0,
                        minimum = 100.0,
                        maximum = 100.0
                ),
                globalPercent = PercentageDistribution(
                        average = 100.0,
                        minimum = 100.0,
                        maximum = 100.0
                ),
                children = listOf(
                        JsonPercentageSizeLeaf(
                                name = "A",
                                localPercent = PercentageDistribution(
                                        average = 70.0,
                                        minimum = 70.0,
                                        maximum = 70.0
                                ),
                                globalPercent = PercentageDistribution(
                                        average = 70.0,
                                        minimum = 70.0,
                                        maximum = 70.0
                                )
                        ),
                        JsonPercentageSizeLeaf(
                                name = "B",
                                localPercent = PercentageDistribution(
                                        average = 30.0,
                                        minimum = 30.0,
                                        maximum = 30.0
                                ),
                                globalPercent = PercentageDistribution(
                                        average = 30.0,
                                        minimum = 30.0,
                                        maximum = 30.0
                                )
                        )
                )
        )
    }

    @Test
    fun `it creates a percentage representation for a complex Object`() {

        val input = JsonSizeObjectOverview(
                name = "test",
                size = Distribution(
                        average = 100,
                        minimum = 100,
                        maximum = 100,
                        standardDeviation = 0.0
                ),
                children = listOf(
                        JsonSizeObjectOverview(
                                name = "A",
                                size = Distribution(
                                        average = 80,
                                        minimum = 80,
                                        maximum = 80,
                                        standardDeviation = 0.0
                                ),
                                children = listOf(
                                        JsonSizeLeafOverview(
                                                name = "C",
                                                size = Distribution(
                                                        average = 20,
                                                        minimum = 20,
                                                        maximum = 20,
                                                        standardDeviation = 0.0
                                                )
                                        ),
                                        JsonSizeLeafOverview(
                                                name = "D",
                                                size = Distribution(
                                                        average = 60,
                                                        minimum = 60,
                                                        maximum = 60,
                                                        standardDeviation = 0.0
                                                )
                                        )
                                )
                        ),
                        JsonSizeLeafOverview(
                                name = "B",
                                size = Distribution(
                                        average = 20,
                                        minimum = 20,
                                        maximum = 20,
                                        standardDeviation = 0.0
                                )
                        )
                )
        )

        percentageSizer.generatePercentage(input) succeedsAndShouldReturn JsonPercentageSizeObject(
                name = "test",
                localPercent = PercentageDistribution(
                        average = 100.0,
                        minimum = 100.0,
                        maximum = 100.0
                ),
                globalPercent = PercentageDistribution(
                        average = 100.0,
                        minimum = 100.0,
                        maximum = 100.0
                ),
                children = listOf(
                        JsonPercentageSizeObject(
                                name = "A",
                                localPercent = PercentageDistribution(
                                        average = 80.0,
                                        minimum = 80.0,
                                        maximum = 80.0
                                ),
                                globalPercent = PercentageDistribution(
                                        average = 80.0,
                                        minimum = 80.0,
                                        maximum = 80.0
                                ),
                                children = listOf(
                                        JsonPercentageSizeLeaf(
                                                name = "C",
                                                localPercent = PercentageDistribution(
                                                        average = 25.0,
                                                        minimum = 25.0,
                                                        maximum = 25.0
                                                ),
                                                globalPercent = PercentageDistribution(
                                                        average = 20.0,
                                                        minimum = 20.0,
                                                        maximum = 20.0
                                                )
                                        ),
                                        JsonPercentageSizeLeaf(
                                                name = "D",
                                                localPercent = PercentageDistribution(
                                                        average = 75.0,
                                                        minimum = 75.0,
                                                        maximum = 75.0
                                                ),
                                                globalPercent = PercentageDistribution(
                                                        average = 60.0,
                                                        minimum = 60.0,
                                                        maximum = 60.0
                                                )
                                        )
                                )
                        ),
                        JsonPercentageSizeLeaf(
                                name = "B",
                                localPercent = PercentageDistribution(
                                        average = 20.0,
                                        minimum = 20.0,
                                        maximum = 20.0
                                ),
                                globalPercent = PercentageDistribution(
                                        average = 20.0,
                                        minimum = 20.0,
                                        maximum = 20.0
                                )
                        )
                )
        )
    }

    @Test
    fun `it creates a percentage representation for an array`() {

        val input = JsonSizeArrayOverview(
                name = "test",
                size = Distribution(
                        average = 100,
                        minimum = 100,
                        maximum = 100,
                        standardDeviation = 0.0
                ),
                averageChild = JsonSizeLeafOverview(
                        name = "A",
                        size = Distribution(
                                average = 20,
                                minimum = 20,
                                maximum = 20,
                                standardDeviation = 0.0
                        )
                ),
                numberOfChildren = Distribution(
                        average = 5,
                        minimum = 5,
                        maximum = 5,
                        standardDeviation = 0.0)

        )

        percentageSizer.generatePercentage(input) succeedsAndShouldReturn JsonPercentageSizeArray(
                name = "test",
                localPercent = PercentageDistribution(
                        average = 100.0,
                        minimum = 100.0,
                        maximum = 100.0
                ),
                globalPercent = PercentageDistribution(
                        average = 100.0,
                        minimum = 100.0,
                        maximum = 100.0
                ),
                averageChild = JsonPercentageSizeLeaf(
                        name = "A",
                        localPercent = PercentageDistribution(
                                average = 20.0,
                                minimum = 20.0,
                                maximum = 20.0
                        ),
                        globalPercent = PercentageDistribution(
                                average = 20.0,
                                minimum = 20.0,
                                maximum = 20.0
                        )
                ),
                numberOfChildren = Distribution(
                        average = 5,
                        minimum = 5,
                        maximum = 5,
                        standardDeviation = 0.0
                )
        )
    }
}