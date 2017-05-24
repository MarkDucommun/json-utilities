package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.succeedsAndShouldReturn
import org.junit.Test

class JsonPercentageSizerTest {

    val percentageSizer = JsonPercentageSizer()

    @Test
    fun `it creates a percentage representation for a Leaf Node`() {

        percentageSizer.generatePercentage(JsonSizeLeafOverview(
                name = "test",
                size = NormalIntDistribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        standardDeviation = 0.0
                )
        )) succeedsAndShouldReturn JsonPercentageSizeLeaf(
                name = "test",
                localPercent = NormalPercentageDistribution(
                        average = 100.0,
                        minimum = 100.0,
                        maximum = 100.0
                ),
                globalPercent = NormalPercentageDistribution(
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
                size = NormalIntDistribution(
                        average = 100,
                        minimum = 100,
                        maximum = 100,
                        standardDeviation = 0.0
                ),
                children = listOf(
                        JsonSizeObjectChild(
                                overview = JsonSizeLeafOverview(
                                        name = "A",
                                        size = NormalIntDistribution(
                                                average = 70,
                                                minimum = 70,
                                                maximum = 70,
                                                standardDeviation = 0.0
                                        )
                                ),
                                presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0))
                        ,
                        JsonSizeObjectChild(
                                overview = JsonSizeLeafOverview(
                                        name = "B",
                                        size = NormalIntDistribution(
                                                average = 30,
                                                minimum = 30,
                                                maximum = 30,
                                                standardDeviation = 0.0
                                        )
                                ),
                                presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0))
                )
        )

        percentageSizer.generatePercentage(input) succeedsAndShouldReturn JsonPercentageSizeObject(
                name = "test",
                localPercent = NormalPercentageDistribution(
                        average = 100.0,
                        minimum = 100.0,
                        maximum = 100.0
                ),
                globalPercent = NormalPercentageDistribution(
                        average = 100.0,
                        minimum = 100.0,
                        maximum = 100.0
                ),
                children = listOf(
                        JsonPercentageSizeLeaf(
                                name = "A",
                                localPercent = NormalPercentageDistribution(
                                        average = 70.0,
                                        minimum = 70.0,
                                        maximum = 70.0
                                ),
                                globalPercent = NormalPercentageDistribution(
                                        average = 70.0,
                                        minimum = 70.0,
                                        maximum = 70.0
                                )
                        ),
                        JsonPercentageSizeLeaf(
                                name = "B",
                                localPercent = NormalPercentageDistribution(
                                        average = 30.0,
                                        minimum = 30.0,
                                        maximum = 30.0
                                ),
                                globalPercent = NormalPercentageDistribution(
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
                size = NormalIntDistribution(
                        average = 100,
                        minimum = 100,
                        maximum = 100,
                        standardDeviation = 0.0
                ),
                children = listOf(
                        JsonSizeObjectChild(
                                overview = JsonSizeObjectOverview(
                                        name = "A",
                                        size = NormalIntDistribution(
                                                average = 80,
                                                minimum = 80,
                                                maximum = 80,
                                                standardDeviation = 0.0
                                        ),
                                        children = listOf(
                                                JsonSizeObjectChild(
                                                        overview = JsonSizeLeafOverview(
                                                                name = "C",
                                                                size = NormalIntDistribution(
                                                                        average = 20,
                                                                        minimum = 20,
                                                                        maximum = 20,
                                                                        standardDeviation = 0.0
                                                                )
                                                        ),
                                                        presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0)
                                                ),
                                                JsonSizeObjectChild(
                                                        overview = JsonSizeLeafOverview(
                                                                name = "D",
                                                                size = NormalIntDistribution(
                                                                        average = 60,
                                                                        minimum = 60,
                                                                        maximum = 60,
                                                                        standardDeviation = 0.0
                                                                )
                                                        ),
                                                        presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0))
                                        )
                                ),
                                presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0)
                        ),
                        JsonSizeObjectChild(
                                overview = JsonSizeLeafOverview(
                                        name = "B",
                                        size = NormalIntDistribution(
                                                average = 20,
                                                minimum = 20,
                                                maximum = 20,
                                                standardDeviation = 0.0
                                        )
                                ),
                                presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0))
                )
        )

        percentageSizer.generatePercentage(input) succeedsAndShouldReturn JsonPercentageSizeObject(
                name = "test",
                localPercent = NormalPercentageDistribution(
                        average = 100.0,
                        minimum = 100.0,
                        maximum = 100.0
                ),
                globalPercent = NormalPercentageDistribution(
                        average = 100.0,
                        minimum = 100.0,
                        maximum = 100.0
                ),
                children = listOf(
                        JsonPercentageSizeObject(
                                name = "A",
                                localPercent = NormalPercentageDistribution(
                                        average = 80.0,
                                        minimum = 80.0,
                                        maximum = 80.0
                                ),
                                globalPercent = NormalPercentageDistribution(
                                        average = 80.0,
                                        minimum = 80.0,
                                        maximum = 80.0
                                ),
                                children = listOf(
                                        JsonPercentageSizeLeaf(
                                                name = "C",
                                                localPercent = NormalPercentageDistribution(
                                                        average = 25.0,
                                                        minimum = 25.0,
                                                        maximum = 25.0
                                                ),
                                                globalPercent = NormalPercentageDistribution(
                                                        average = 20.0,
                                                        minimum = 20.0,
                                                        maximum = 20.0
                                                )
                                        ),
                                        JsonPercentageSizeLeaf(
                                                name = "D",
                                                localPercent = NormalPercentageDistribution(
                                                        average = 75.0,
                                                        minimum = 75.0,
                                                        maximum = 75.0
                                                ),
                                                globalPercent = NormalPercentageDistribution(
                                                        average = 60.0,
                                                        minimum = 60.0,
                                                        maximum = 60.0
                                                )
                                        )
                                )
                        ),
                        JsonPercentageSizeLeaf(
                                name = "B",
                                localPercent = NormalPercentageDistribution(
                                        average = 20.0,
                                        minimum = 20.0,
                                        maximum = 20.0
                                ),
                                globalPercent = NormalPercentageDistribution(
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
                size = NormalIntDistribution(
                        average = 100,
                        minimum = 100,
                        maximum = 100,
                        standardDeviation = 0.0
                ),
                averageChild = JsonSizeLeafOverview(
                        name = "A",
                        size = NormalIntDistribution(
                                average = 20,
                                minimum = 20,
                                maximum = 20,
                                standardDeviation = 0.0
                        )
                ),
                numberOfChildren = NormalIntDistribution(
                        average = 5,
                        minimum = 5,
                        maximum = 5,
                        standardDeviation = 0.0)

        )

        percentageSizer.generatePercentage(input) succeedsAndShouldReturn JsonPercentageSizeArray(
                name = "test",
                localPercent = NormalPercentageDistribution(
                        average = 100.0,
                        minimum = 100.0,
                        maximum = 100.0
                ),
                globalPercent = NormalPercentageDistribution(
                        average = 100.0,
                        minimum = 100.0,
                        maximum = 100.0
                ),
                averageChild = JsonPercentageSizeLeaf(
                        name = "A",
                        localPercent = NormalPercentageDistribution(
                                average = 20.0,
                                minimum = 20.0,
                                maximum = 20.0
                        ),
                        globalPercent = NormalPercentageDistribution(
                                average = 20.0,
                                minimum = 20.0,
                                maximum = 20.0
                        )
                ),
                numberOfChildren = NormalIntDistribution(
                        average = 5,
                        minimum = 5,
                        maximum = 5,
                        standardDeviation = 0.0
                )
        )
    }
}