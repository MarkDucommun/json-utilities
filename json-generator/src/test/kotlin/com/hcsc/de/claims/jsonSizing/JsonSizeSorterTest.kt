package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.succeedsAndShouldReturn
import org.junit.Test

class JsonSizeSorterTest {

    val sorter = JsonSizeSorter()

    @Test
    fun `it sorts a LeafNode by average size`() {

        sorter.sort(JsonSizeLeafOverview(name = "trial", size = NormalIntDistribution(
                average = 1,
                minimum = 1,
                maximum = 1,
                standardDeviation = 0.0
        ))) succeedsAndShouldReturn JsonSizeLeafOverview(name = "trial", size = NormalIntDistribution(
                average = 1,
                minimum = 1,
                maximum = 1,
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
                        standardDeviation = 0.0
                ),
                children = listOf(
                        JsonSizeObjectChild(
                                overview = JsonSizeLeafOverview(name = "A", size = NormalIntDistribution(
                                        average = 1,
                                        minimum = 1,
                                        maximum = 1,
                                        standardDeviation = 0.0
                                )),
                                presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0)
                        ),
                        JsonSizeObjectChild(
                                overview = JsonSizeLeafOverview(name = "B", size = NormalIntDistribution(
                                        average = 2,
                                        minimum = 2,
                                        maximum = 2,
                                        standardDeviation = 0.0
                                )),
                                presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0)
                        ))
        )

        sorter.sort(input) succeedsAndShouldReturn JsonSizeObjectOverview(
                name = "top",
                size = NormalIntDistribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        standardDeviation = 0.0
                ),
                children = listOf(
                        JsonSizeObjectChild(
                                overview = JsonSizeLeafOverview(name = "B", size = NormalIntDistribution(
                                        average = 2,
                                        minimum = 2,
                                        maximum = 2,
                                        standardDeviation = 0.0
                                )),
                                presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0)
                        ),
                        JsonSizeObjectChild(
                                overview = JsonSizeLeafOverview(name = "A", size = NormalIntDistribution(
                                        average = 1,
                                        minimum = 1,
                                        maximum = 1,
                                        standardDeviation = 0.0
                                )),
                                presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0)
                        ))
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
                        standardDeviation = 0.0
                ),
                averageChild = JsonSizeObjectOverview(
                        name = "child",
                        size = NormalIntDistribution(
                                average = 1,
                                minimum = 1,
                                maximum = 1,
                                standardDeviation = 0.0
                        ),
                        children = listOf(
                                JsonSizeObjectChild(
                                        overview = JsonSizeLeafOverview(name = "A", size = NormalIntDistribution(
                                                average = 1,
                                                minimum = 1,
                                                maximum = 1,
                                                standardDeviation = 0.0
                                        )),
                                        presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0)
                                ),
                                JsonSizeObjectChild(
                                        overview = JsonSizeLeafOverview(name = "B", size = NormalIntDistribution(
                                                average = 2,
                                                minimum = 2,
                                                maximum = 2,
                                                standardDeviation = 0.0
                                        )),
                                        presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0)
                                ))
                ),
                numberOfChildren = NormalIntDistribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        standardDeviation = 0.0
                )
        )

        sorter.sort(array) succeedsAndShouldReturn JsonSizeArrayOverview(
                name = "top",
                size = NormalIntDistribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        standardDeviation = 0.0
                ),
                averageChild = JsonSizeObjectOverview(
                        name = "child",
                        size = NormalIntDistribution(
                                average = 1,
                                minimum = 1,
                                maximum = 1,
                                standardDeviation = 0.0
                        ),
                        children = listOf(
                                JsonSizeObjectChild(
                                        overview = JsonSizeLeafOverview(name = "B", size = NormalIntDistribution(
                                                average = 2,
                                                minimum = 2,
                                                maximum = 2,
                                                standardDeviation = 0.0
                                        )),
                                        presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0)
                                ),
                                JsonSizeObjectChild(
                                        overview = JsonSizeLeafOverview(name = "A", size = NormalIntDistribution(
                                                average = 1,
                                                minimum = 1,
                                                maximum = 1,
                                                standardDeviation = 0.0
                                        ))
                                        ,
                                        presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0)
                                ))
                ),
                numberOfChildren = NormalIntDistribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
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
                        standardDeviation = 0.0
                ),
                children = listOf(JsonSizeObjectChild(
                        overview = JsonSizeObjectOverview(
                                name = "child",
                                size = NormalIntDistribution(
                                        average = 1,
                                        minimum = 1,
                                        maximum = 1,
                                        standardDeviation = 0.0
                                ),
                                children = listOf(
                                        JsonSizeObjectChild(
                                                overview = JsonSizeLeafOverview(name = "A", size = NormalIntDistribution(
                                                        average = 1,
                                                        minimum = 1,
                                                        maximum = 1,
                                                        standardDeviation = 0.0
                                                )),
                                                presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0)
                                        ),
                                        JsonSizeObjectChild(
                                                overview = JsonSizeLeafOverview(name = "B", size = NormalIntDistribution(
                                                        average = 2,
                                                        minimum = 2,
                                                        maximum = 2,
                                                        standardDeviation = 0.0
                                                )),
                                                presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0)
                                        ))
                        ),
                        presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0)
                ))
        )

        sorter.sort(complexObject) succeedsAndShouldReturn JsonSizeObjectOverview(
                name = "top",
                size = NormalIntDistribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        standardDeviation = 0.0
                ),
                children = listOf(JsonSizeObjectChild(
                        overview = JsonSizeObjectOverview(
                                name = "child",
                                size = NormalIntDistribution(
                                        average = 1,
                                        minimum = 1,
                                        maximum = 1,
                                        standardDeviation = 0.0
                                ),
                                children = listOf(
                                        JsonSizeObjectChild(
                                                overview = JsonSizeLeafOverview(name = "B", size = NormalIntDistribution(
                                                        average = 2,
                                                        minimum = 2,
                                                        maximum = 2,
                                                        standardDeviation = 0.0
                                                )),
                                                presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0)
                                        ),
                                        JsonSizeObjectChild(
                                                overview = JsonSizeLeafOverview(name = "A", size = NormalIntDistribution(
                                                        average = 1,
                                                        minimum = 1,
                                                        maximum = 1,
                                                        standardDeviation = 0.0
                                                )),
                                                presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0)
                                        )
                                )
                        ),
                        presence = NormalDoubleDistribution(average = 1.0, minimum = 1.0, maximum = 1.0, standardDeviation = 0.0)
                ))
        )
    }
}