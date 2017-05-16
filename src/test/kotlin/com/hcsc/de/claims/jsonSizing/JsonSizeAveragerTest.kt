package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.SingleResult
import com.hcsc.de.claims.failsAnd
import com.hcsc.de.claims.succeedsAnd
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Ignore
import org.junit.Test

class JsonSizeAveragerTest {

    val jsonSizeAverager = JsonSizeAverager()

    @Test
    fun `it cannot sum JsonSizeNodes that are different types (except for leaf nodes to empty objects)`() {

        val node1 = JsonSizeLeafNode(name = "A", size = 10)
        val node2 = JsonSizeObject(name = "A", size = 15, children = listOf(JsonSizeLeafNode(name = "A", size = 10)), averageChildSize = 0)

        jsonSizeAverager.generateJsonSizeOverview(node1, node2).blockingGet() failsAnd { message ->

            assertThat(message).isEqualTo("Nodes are not the same type")
        }
    }

    @Test
    fun `it can sum JsonSizeLeafNodes and JsonSizeEmpty`() {

        val node1 = JsonSizeLeafNode(name = "A", size = 10)
        val node2 = JsonSizeEmpty(name = "A")

        jsonSizeAverager.generateJsonSizeOverview(node1, node2) succeedsAnd { averageNode ->

            assertThat(averageNode).isEqualTo(JsonSizeLeafOverview(
                    name = "A",
                    size = Distribution(
                            average = 5,
                            minimum = 0,
                            maximum = 10,
                            standardDeviation = 5.0
                    )
            ))
        }
    }

    @Test
    fun `it can sum JsonSizeObject with JsonSizeEmpty`() {

        val node1 = JsonSizeObject(name = "A", size = 10, children = emptyList(), averageChildSize = 0)
        val node2 = JsonSizeEmpty(name = "A")

        jsonSizeAverager.generateJsonSizeOverview(node1, node2) succeedsAnd { averageNode ->

            assertThat(averageNode).isEqualTo(JsonSizeObjectOverview(
                    name = "A",
                    size = Distribution(
                            average = 5,
                            minimum = 0,
                            maximum = 10,
                            standardDeviation = 5.0
                    ),
                    children = emptyList()
            ))
        }

    }

    @Test
    fun `it can sum JsonSizeArray and JsonSizeEmpty`() {

        val node1 = JsonSizeEmpty(name = "A")
        val node2 = JsonSizeArray(
                name = "A",
                size = 15,
                children = listOf(JsonSizeLeafNode(name = "A", size = 10)),
                averageChildSize = 0
        )

        jsonSizeAverager.generateJsonSizeOverview(node1, node2) succeedsAnd { averageNode ->

            assertThat(averageNode).isEqualToComparingFieldByFieldRecursively(JsonSizeArrayOverview(
                    name = "A",
                    size = Distribution(
                            average = 7,
                            minimum = 0,
                            maximum = 15,
                            standardDeviation = 7.516648189186454
                    ),
                    numberOfChildren = Distribution(
                            average = 1,
                            minimum = 0,
                            maximum = 1,
                            standardDeviation = 0.7071067811865476
                    ),
                    averageChild = JsonSizeLeafOverview(
                            name = "averageChild",
                            size = Distribution(
                                    average = 10,
                                    minimum = 10,
                                    maximum = 10,
                                    standardDeviation = 0.0
                            )
                    )
            ))
        }
    }


    @Test
    fun `it cannot sum JsonSizeNodes that are named differently`() {

        val node1 = JsonSizeLeafNode(name = "A", size = 10)
        val node2 = JsonSizeLeafNode(name = "B", size = 15)

        jsonSizeAverager.generateJsonSizeOverview(node1, node2) failsAnd { message ->

            assertThat(message).isEqualTo("Nodes do not match")
        }
    }

    @Test
    fun `it cannot sum JsonSizeObjects that are shaped differently`() {

        val node1 = JsonSizeObject(
                name = "A",
                size = 15,
                children = listOf(JsonSizeLeafNode(name = "A", size = 10)),
                averageChildSize = 10
        )
        val node2 = JsonSizeObject(
                name = "A",
                size = 15,
                children = listOf(JsonSizeLeafNode(name = "B", size = 15)),
                averageChildSize = 10
        )

        jsonSizeAverager.generateJsonSizeOverview(node1, node2) failsAnd { message ->

            assertThat(message).isEqualTo("Nodes do not match")
        }
    }

    @Test
    fun `it cannot sum JsonSizeObjects that have children with different key types`() {

        val node1 = JsonSizeObject(
                name = "A",
                size = 15,
                children = listOf(JsonSizeLeafNode(name = "A", size = 10)),
                averageChildSize = 10
        )
        val node2 = JsonSizeObject(
                name = "A",
                size = 15,
                children = listOf(JsonSizeObject(name = "A", size = 15, children = listOf(JsonSizeLeafNode(name = "A", size = 10)), averageChildSize = 10)),
                averageChildSize = 10
        )

        jsonSizeAverager.generateJsonSizeOverview(node1, node2) failsAnd { message ->

            assertThat(message).isEqualTo("Nodes are not the same type")
        }
    }

    @Test
    @Ignore("TODO NOT A VALID TEST, DO WE NEED SOME FORM OF ARRAY CHILD TO PROHIBIT THIS SETUP FROM EVER HAPPENING?")
    fun `it cannot sum JsonSizeArrays that have different types of children in any given Array`() {

        val node1 = JsonSizeArray(
                name = "A",
                size = 15,
                children = listOf(
                        JsonSizeLeafNode(name = "A", size = 10),
                        JsonSizeLeafNode(name = "B", size = 10)
                ),
                averageChildSize = 10
        )
        val node2 = JsonSizeArray(
                name = "A",
                size = 15,
                children = listOf(JsonSizeLeafNode(name = "B", size = 15)),
                averageChildSize = 10
        )

        jsonSizeAverager.generateJsonSizeOverview(node1, node2) failsAnd { message ->

            assertThat(message).isEqualTo("Nodes do not match")
        }
    }

    @Test
    @Ignore("TODO NOT A VALID TEST, DO WE NEED SOME FORM OF ARRAY CHILD TO PROHIBIT THIS SETUP FROM EVER HAPPENING?")
    fun `it cannot sum JsonSizeArrays that are shaped differently`() {

        val node1 = JsonSizeArray(
                name = "A",
                size = 15,
                children = listOf(JsonSizeLeafNode(name = "0", size = 10)),
                averageChildSize = 10
        )
        val node2 = JsonSizeArray(
                name = "A",
                size = 15,
                children = listOf(JsonSizeLeafNode(name = "1", size = 15)),
                averageChildSize = 10
        )

        jsonSizeAverager.generateJsonSizeOverview(node1, node2) failsAnd { message ->

            assertThat(message).isEqualTo("Nodes do not match")
        }
    }

    @Test
    fun `it can sum a list of simple JsonSizeLeafNodes`() {

        val node1 = JsonSizeLeafNode(name = "A", size = 10)
        val node2 = JsonSizeLeafNode(name = "A", size = 15)

        jsonSizeAverager.generateJsonSizeOverview(node1, node2) succeedsAnd { averageNode ->

            assertThat(averageNode).isEqualTo(JsonSizeLeafOverview(name = "A", size = Distribution(
                    average = 13,
                    minimum = 10,
                    maximum = 15,
                    standardDeviation = 2.5495097567963922
            )))
        }
    }

    @Test
    fun `it can sum a list of JsonSizeObjects`() {

        val node1 = JsonSizeObject(
                name = "A",
                size = 15,
                children = listOf(JsonSizeLeafNode(name = "B", size = 10)),
                averageChildSize = 10
        )
        val node2 = JsonSizeObject(
                name = "A",
                size = 24,
                children = listOf(JsonSizeLeafNode(name = "B", size = 19)),
                averageChildSize = 19
        )

        jsonSizeAverager.generateJsonSizeOverview(node1, node2) succeedsAnd { averageNode ->

            assertThat(averageNode).isEqualTo(JsonSizeObjectOverview(
                    name = "A",
                    size = Distribution(
                            average = 19,
                            minimum = 15,
                            maximum = 24,
                            standardDeviation = 4.527692569068709
                    ),
                    children = listOf(JsonSizeLeafOverview(name = "B", size = Distribution(
                            average = 15,
                            minimum = 10,
                            maximum = 19,
                            standardDeviation = 4.527692569068709
                    )))
            ))
        }
    }

    @Test
    fun `it can sum a list of JsonSizeNodes with JsonSizeArrays to create an averaged node`() {

        val node1 = JsonSizeArray(
                name = "top",
                size = 48,
                children = listOf(
                        JsonSizeObject(name = "0", size = 20,
                                children = listOf(
                                        JsonSizeLeafNode(name = "A", size = 4),
                                        JsonSizeLeafNode(name = "B", size = 5)

                                ),
                                averageChildSize = 5
                        ),
                        JsonSizeObject(name = "1", size = 25,
                                children = listOf(
                                        JsonSizeLeafNode(name = "A", size = 6),
                                        JsonSizeLeafNode(name = "B", size = 8)
                                ),
                                averageChildSize = 7
                        )
                ),
                averageChildSize = 9
        )

        val node2 = JsonSizeArray(
                name = "top",
                size = 63,
                children = listOf(
                        JsonSizeObject(name = "0", size = 25,
                                children = listOf(
                                        JsonSizeLeafNode(name = "A", size = 6),
                                        JsonSizeLeafNode(name = "B", size = 8)
                                ),
                                averageChildSize = 7
                        ),
                        JsonSizeObject(name = "1", size = 35,
                                children = listOf(
                                        JsonSizeLeafNode(name = "A", size = 10),
                                        JsonSizeLeafNode(name = "B", size = 14)
                                ),
                                averageChildSize = 12
                        ),
                        JsonSizeObject(name = "1", size = 35,
                                children = listOf(
                                        JsonSizeLeafNode(name = "A", size = 20),
                                        JsonSizeLeafNode(name = "B", size = 14)
                                ),
                                averageChildSize = 12
                        )
                ),
                averageChildSize = 9
        )

        val result = jsonSizeAverager.generateJsonSizeOverview(node1, node2)

        result succeedsAnd { averagedNode ->

            assertThat(averagedNode).isEqualToComparingFieldByFieldRecursively(
                    JsonSizeArrayOverview(
                            name = "top",
                            size = Distribution(
                                    average = 55,
                                    minimum = 48,
                                    maximum = 63,
                                    standardDeviation = 7.516648189186454
                            ),
                            averageChild = JsonSizeObjectOverview(
                                    name = "averageChild",
                                    size = Distribution(
                                            average = 28,
                                            minimum = 20,
                                            maximum = 35,
                                            standardDeviation = 6.0
                                    ),
                                    children = listOf(
                                            JsonSizeLeafOverview(name = "A", size = Distribution(
                                                    average = 9,
                                                    minimum = 4,
                                                    maximum = 20,
                                                    standardDeviation = 5.744562646538029
                                            )),
                                            JsonSizeLeafOverview(name = "B", size = Distribution(
                                                    average = 9,
                                                    minimum = 5,
                                                    maximum = 14,
                                                    standardDeviation = 3.687817782917155
                                            ))
                                    )
                            ),
                            numberOfChildren = Distribution(
                                    average = 3,
                                    minimum = 2,
                                    maximum = 3,
                                    standardDeviation = 0.7071067811865476
                            )
                    )
            )
        }
    }


    @Test
    fun `it can handle empty JsonSizeArrays`() {

        val node1 = JsonSizeArray(
                name = "top",
                size = 48,
                children = emptyList(),
                averageChildSize = 0
        )

        val node2 = JsonSizeArray(
                name = "top",
                size = 63,
                children = listOf(
                        JsonSizeObject(name = "0", size = 25,
                                children = listOf(
                                        JsonSizeLeafNode(name = "A", size = 6),
                                        JsonSizeLeafNode(name = "B", size = 8)
                                ),
                                averageChildSize = 7
                        ),
                        JsonSizeObject(name = "1", size = 35,
                                children = listOf(
                                        JsonSizeLeafNode(name = "A", size = 10),
                                        JsonSizeLeafNode(name = "B", size = 14)
                                ),
                                averageChildSize = 12
                        ),
                        JsonSizeObject(name = "1", size = 35,
                                children = listOf(
                                        JsonSizeLeafNode(name = "A", size = 20),
                                        JsonSizeLeafNode(name = "B", size = 14)
                                ),
                                averageChildSize = 12
                        )
                ),
                averageChildSize = 9
        )

        val result = jsonSizeAverager.generateJsonSizeOverview(node1, node2)

        result succeedsAnd { averagedNode ->

            assertThat(averagedNode).isEqualToComparingFieldByFieldRecursively(
                    JsonSizeArrayOverview(
                            name = "top",
                            size = Distribution(
                                    average = 55,
                                    minimum = 48,
                                    maximum = 63,
                                    standardDeviation = 7.516648189186454
                            ),
                            averageChild = JsonSizeObjectOverview(
                                    name = "averageChild",
                                    size = Distribution(
                                            average = 31,
                                            minimum = 25,
                                            maximum = 35,
                                            standardDeviation = 4.760952285695233
                                    ),
                                    children = listOf(
                                            JsonSizeLeafOverview(name = "A", size = Distribution(
                                                    average = 12,
                                                    minimum = 6,
                                                    maximum = 20,
                                                    standardDeviation = 5.887840577551898
                                            )),
                                            JsonSizeLeafOverview(name = "B", size = Distribution(
                                                    average = 12,
                                                    minimum = 8,
                                                    maximum = 14,
                                                    standardDeviation = 2.8284271247461903
                                            ))
                                    )
                            ),
                            numberOfChildren = Distribution(
                                    average = 1,
                                    minimum = 0,
                                    maximum = 3,
                                    standardDeviation = 1.5811388300841898
                            )
                    )
            )
        }
    }

    private fun JsonSizeAverager.generateJsonSizeOverview(vararg nodes: JsonSizeNode): SingleResult<String, JsonSizeOverview> {

        return generateJsonSizeOverview(nodes = nodes.asList())
    }
}