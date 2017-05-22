package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.succeedsAndShouldReturn
import org.junit.Test

class JsonSizeSorterTest {

    val sorter = JsonSizeSorter()

    @Test
    fun `it sorts a LeafNode`() {

        sorter.sort(JsonSizeLeafOverview(name = "trial", size = Distribution(
                average = 1,
                minimum = 1,
                maximum = 1,
                standardDeviation = 0.0
        ))) succeedsAndShouldReturn JsonSizeLeafOverview(name = "trial", size = Distribution(
                average = 1,
                minimum = 1,
                maximum = 1,
                standardDeviation = 0.0
        ))
    }

    @Test
    fun `it sorts an Object`() {

        val input = JsonSizeObjectOverview(
                name = "top",
                size = Distribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        standardDeviation = 0.0
                ),
                children = listOf(
                        JsonSizeLeafOverview(name = "A", size = Distribution(
                                average = 1,
                                minimum = 1,
                                maximum = 1,
                                standardDeviation = 0.0
                        )),
                        JsonSizeLeafOverview(name = "B", size = Distribution(
                                average = 2,
                                minimum = 2,
                                maximum = 2,
                                standardDeviation = 0.0
                        )))
        )

        sorter.sort(input) succeedsAndShouldReturn JsonSizeObjectOverview(
                name = "top",
                size = Distribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        standardDeviation = 0.0
                ),
                children = listOf(
                        JsonSizeLeafOverview(name = "B", size = Distribution(
                                average = 2,
                                minimum = 2,
                                maximum = 2,
                                standardDeviation = 0.0
                        )),
                        JsonSizeLeafOverview(name = "A", size = Distribution(
                                average = 1,
                                minimum = 1,
                                maximum = 1,
                                standardDeviation = 0.0
                        )))
        )
    }

    @Test
    fun `it sorts an Array`() {

        val array = JsonSizeArrayOverview(
                name = "top",
                size = Distribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        standardDeviation = 0.0
                ),
                averageChild = JsonSizeObjectOverview(
                        name = "child",
                        size = Distribution(
                                average = 1,
                                minimum = 1,
                                maximum = 1,
                                standardDeviation = 0.0
                        ),
                        children = listOf(
                                JsonSizeLeafOverview(name = "A", size = Distribution(
                                        average = 1,
                                        minimum = 1,
                                        maximum = 1,
                                        standardDeviation = 0.0
                                )),
                                JsonSizeLeafOverview(name = "B", size = Distribution(
                                        average = 2,
                                        minimum = 2,
                                        maximum = 2,
                                        standardDeviation = 0.0
                                )))
                ),
                numberOfChildren = Distribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        standardDeviation = 0.0
                )
        )

        sorter.sort(array) succeedsAndShouldReturn JsonSizeArrayOverview(
                name = "top",
                size = Distribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        standardDeviation = 0.0
                ),
                averageChild = JsonSizeObjectOverview(
                        name = "child",
                        size = Distribution(
                                average = 1,
                                minimum = 1,
                                maximum = 1,
                                standardDeviation = 0.0
                        ),
                        children = listOf(
                                JsonSizeLeafOverview(name = "B", size = Distribution(
                                        average = 2,
                                        minimum = 2,
                                        maximum = 2,
                                        standardDeviation = 0.0
                                )),
                                JsonSizeLeafOverview(name = "A", size = Distribution(
                                        average = 1,
                                        minimum = 1,
                                        maximum = 1,
                                        standardDeviation = 0.0
                                ))
                        )
                ),
                numberOfChildren = Distribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        standardDeviation = 0.0
                )
        )
    }

    @Test
    fun `it sorts an object of objects`() {

        val complexObject = JsonSizeObjectOverview(
                name = "top",
                size = Distribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        standardDeviation = 0.0
                ),
                children = listOf(JsonSizeObjectOverview(
                        name = "child",
                        size = Distribution(
                                average = 1,
                                minimum = 1,
                                maximum = 1,
                                standardDeviation = 0.0
                        ),
                        children = listOf(
                                JsonSizeLeafOverview(name = "A", size = Distribution(
                                        average = 1,
                                        minimum = 1,
                                        maximum = 1,
                                        standardDeviation = 0.0
                                )),
                                JsonSizeLeafOverview(name = "B", size = Distribution(
                                        average = 2,
                                        minimum = 2,
                                        maximum = 2,
                                        standardDeviation = 0.0
                                )))
                ))
        )

        sorter.sort(complexObject) succeedsAndShouldReturn JsonSizeObjectOverview(
                name = "top",
                size = Distribution(
                        average = 1,
                        minimum = 1,
                        maximum = 1,
                        standardDeviation = 0.0
                ),
                children = listOf(JsonSizeObjectOverview(
                        name = "child",
                        size = Distribution(
                                average = 1,
                                minimum = 1,
                                maximum = 1,
                                standardDeviation = 0.0
                        ),
                        children = listOf(
                                JsonSizeLeafOverview(name = "B", size = Distribution(
                                        average = 2,
                                        minimum = 2,
                                        maximum = 2,
                                        standardDeviation = 0.0
                                )),
                                JsonSizeLeafOverview(name = "A", size = Distribution(
                                        average = 1,
                                        minimum = 1,
                                        maximum = 1,
                                        standardDeviation = 0.0
                                )))
                ))
        )
    }
}