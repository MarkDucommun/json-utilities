package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.distributions.parametric.NormalIntDistribution
import com.hcsc.de.claims.distributions.RatioProbability
import com.hcsc.de.claims.results.get
import com.hcsc.de.claims.results.succeedsAndShouldReturn
import org.junit.Test

class JsonSizeSorterTest {

    val sorter = JsonSizeSorter()

    @Test
    fun `it sorts a LeafNode by average size`() {

        sorter.sort(JsonSizeLeafOverview(name = "trial", size = NormalIntDistribution(
                average = 1,
                minimum = 1,
                maximum = 1,
                mode = 1,
                median = 1,
                standardDeviation = 0.0
        ))) succeedsAndShouldReturn JsonSizeLeafOverview(name = "trial", size = NormalIntDistribution(
                average = 1,
                minimum = 1,
                maximum = 1,
                mode = 1,
                median = 1,
                standardDeviation = 0.0
        ))
    }

    @Test
    fun `it sorts an Object by average size`() {

        val input = JsonSizeObjectOverview(
                name = "top",
                size = NormalIntDistribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        mode = 1,
                        median = 1,
                        standardDeviation = 0.0
                ),
                children = listOf(
                        JsonSizeObjectChild(
                                overview = JsonSizeLeafOverview(name = "A", size = NormalIntDistribution(
                                        average = 1,
                                        minimum = 1,
                                        maximum = 1,
                                        mode = 1,
                                        median = 1,
                                        standardDeviation = 0.0
                                )),
                                presence = fullProbability),
                        JsonSizeObjectChild(
                                overview = JsonSizeLeafOverview(name = "B", size = NormalIntDistribution(
                                        average = 2,
                                        minimum = 2,
                                        maximum = 2,
                                        mode = 2,
                                        median = 2,
                                        standardDeviation = 0.0
                                )),
                                presence = fullProbability))
        )

        sorter.sort(input) succeedsAndShouldReturn JsonSizeObjectOverview(
                name = "top",
                size = NormalIntDistribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        mode = 1,
                        median = 1,
                        standardDeviation = 0.0
                ),
                children = listOf(
                        JsonSizeObjectChild(
                                overview = JsonSizeLeafOverview(name = "B", size = NormalIntDistribution(
                                        average = 2,
                                        minimum = 2,
                                        maximum = 2,
                                        mode = 2,
                                        median = 2,
                                        standardDeviation = 0.0
                                )),
                                presence = fullProbability),
                        JsonSizeObjectChild(
                                overview = JsonSizeLeafOverview(name = "A", size = NormalIntDistribution(
                                        average = 1,
                                        minimum = 1,
                                        maximum = 1,
                                        mode = 1,
                                        median = 1,
                                        standardDeviation = 0.0
                                )),
                                presence = fullProbability))
        )
    }

    @Test
    fun `it sorts an Array by average size`() {

        val array = JsonSizeArrayOverview(
                name = "top",
                size = NormalIntDistribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        mode = 1,
                        median = 1,
                        standardDeviation = 0.0
                ),
                averageChild = JsonSizeObjectOverview(
                        name = "child",
                        size = NormalIntDistribution(
                                average = 1,
                                minimum = 1,
                                maximum = 1,
                                mode = 1,
                                median = 1,
                                standardDeviation = 0.0
                        ),
                        children = listOf(
                                JsonSizeObjectChild(
                                        overview = JsonSizeLeafOverview(name = "A", size = NormalIntDistribution(
                                                average = 1,
                                                minimum = 1,
                                                maximum = 1,
                                                mode = 1,
                                                median = 1,
                                                standardDeviation = 0.0
                                        )),
                                        presence = fullProbability),
                                JsonSizeObjectChild(
                                        overview = JsonSizeLeafOverview(name = "B", size = NormalIntDistribution(
                                                average = 2,
                                                minimum = 2,
                                                maximum = 2,
                                                mode = 2,
                                                median = 2,
                                                standardDeviation = 0.0
                                        )),
                                        presence = fullProbability))
                ),
                numberOfChildren = NormalIntDistribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        mode = 1,
                        median = 1,
                        standardDeviation = 0.0
                )
        )

        sorter.sort(array) succeedsAndShouldReturn JsonSizeArrayOverview(
                name = "top",
                size = NormalIntDistribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        mode = 1,
                        median = 1,
                        standardDeviation = 0.0
                ),
                averageChild = JsonSizeObjectOverview(
                        name = "child",
                        size = NormalIntDistribution(
                                average = 1,
                                minimum = 1,
                                maximum = 1,
                                mode = 1,
                                median = 1,
                                standardDeviation = 0.0
                        ),
                        children = listOf(
                                JsonSizeObjectChild(
                                        overview = JsonSizeLeafOverview(name = "B", size = NormalIntDistribution(
                                                average = 2,
                                                minimum = 2,
                                                maximum = 2,
                                                mode = 2,
                                                median = 2,
                                                standardDeviation = 0.0
                                        )),
                                        presence = fullProbability),
                                JsonSizeObjectChild(
                                        overview = JsonSizeLeafOverview(name = "A", size = NormalIntDistribution(
                                                average = 1,
                                                minimum = 1,
                                                maximum = 1,
                                                mode = 1,
                                                median = 1,
                                                standardDeviation = 0.0
                                        ))
                                        ,
                                        presence = fullProbability))
                ),
                numberOfChildren = NormalIntDistribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        mode = 1,
                        median = 1,
                        standardDeviation = 0.0
                )
        )
    }

    @Test
    fun `it sorts an object of objects by average size`() {

        val complexObject = JsonSizeObjectOverview(
                name = "top",
                size = NormalIntDistribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        mode = 1,
                        median = 1,
                        standardDeviation = 0.0
                ),
                children = listOf(JsonSizeObjectChild(
                        overview = JsonSizeObjectOverview(
                                name = "child",
                                size = NormalIntDistribution(
                                        average = 1,
                                        minimum = 1,
                                        maximum = 1,
                                        mode = 1,
                                        median = 1,
                                        standardDeviation = 0.0
                                ),
                                children = listOf(
                                        JsonSizeObjectChild(
                                                overview = JsonSizeLeafOverview(name = "A", size = NormalIntDistribution(
                                                        average = 1,
                                                        minimum = 1,
                                                        maximum = 1,
                                                        mode = 1,
                                                        median = 1,
                                                        standardDeviation = 0.0
                                                )),
                                                presence = fullProbability),
                                        JsonSizeObjectChild(
                                                overview = JsonSizeLeafOverview(name = "B", size = NormalIntDistribution(
                                                        average = 2,
                                                        minimum = 2,
                                                        maximum = 2,
                                                        mode = 2,
                                                        median = 2,
                                                        standardDeviation = 0.0
                                                )),
                                                presence = fullProbability))
                        ),
                        presence = fullProbability))
        )

        sorter.sort(complexObject) succeedsAndShouldReturn JsonSizeObjectOverview(
                name = "top",
                size = NormalIntDistribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        mode = 1,
                        median = 1,
                        standardDeviation = 0.0
                ),
                children = listOf(JsonSizeObjectChild(
                        overview = JsonSizeObjectOverview(
                                name = "child",
                                size = NormalIntDistribution(
                                        average = 1,
                                        minimum = 1,
                                        maximum = 1,
                                        mode = 1,
                                        median = 1,
                                        standardDeviation = 0.0
                                ),
                                children = listOf(
                                        JsonSizeObjectChild(
                                                overview = JsonSizeLeafOverview(name = "B", size = NormalIntDistribution(
                                                        average = 2,
                                                        minimum = 2,
                                                        maximum = 2,
                                                        mode = 2,
                                                        median = 2,
                                                        standardDeviation = 0.0
                                                )),
                                                presence = fullProbability),
                                        JsonSizeObjectChild(
                                                overview = JsonSizeLeafOverview(name = "A", size = NormalIntDistribution(
                                                        average = 1,
                                                        minimum = 1,
                                                        maximum = 1,
                                                        mode = 1,
                                                        median = 1,
                                                        standardDeviation = 0.0
                                                )),
                                                presence = fullProbability)
                                )
                        ),
                        presence = fullProbability))
        )
    }

    val fullProbability = RatioProbability.create(1.0).get
}