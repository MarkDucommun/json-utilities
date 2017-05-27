package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.failsAnd
import com.hcsc.de.claims.helpers.SingleResult
import com.hcsc.de.claims.succeedsAnd
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Ignore
import org.junit.Test

class JsonSizeAnalyzerTest {

    val jsonSizeAnalyzer = JsonSizeAnalyzer()

    @Test
    fun `it cannot sum JsonSizeNodes that are different non-empty types`() {

        val node1 = JsonSizeLeafNode(name = "A", size = 10)
        val node2 = JsonSizeObject(name = "A", size = 15, children = listOf(JsonSizeLeafNode(name = "A", size = 10)))

        jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2) failsAnd { message: String ->

            assertThat(message).isEqualTo("Nodes are not the same type")
        }
    }

    @Test
    fun `it can sum JsonSizeLeafNodes and JsonSizeEmpty`() {

        val node1 = JsonSizeLeafNode(name = "A", size = 10)
        val node2 = JsonSizeEmpty(name = "A")

        jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2) succeedsAnd {

            assertThat(it).isEqualTo(JsonSizeLeafOverview(
                    name = "A",
                    size = NormalIntDistribution(
                            average = 5,
                            minimum = 0,
                            maximum = 10,
                            mode = 10,
                            median = 5,
                            standardDeviation = 5.0
                    )
            ))
        }
    }

    @Test
    fun `it can sum JsonSizeObject with JsonSizeEmpty`() {

        val node1 = JsonSizeObject(name = "A", size = 10, children = emptyList())
        val node2 = JsonSizeEmpty(name = "A")

        jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2) succeedsAnd {

            assertThat(it).isEqualTo(JsonSizeObjectOverview(
                    name = "A",
                    size = NormalIntDistribution(
                            average = 5,
                            minimum = 0,
                            maximum = 10,
                            mode = 10,
                            median = 5,
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
                children = listOf(JsonSizeLeafNode(name = "A", size = 10))
        )

        jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2) succeedsAnd {

            assertThat(it).isEqualToComparingFieldByFieldRecursively(JsonSizeArrayOverview(
                    name = "A",
                    size = NormalIntDistribution(
                            average = 7,
                            minimum = 0,
                            maximum = 15,
                            mode = 15,
                            median = 7,
                            standardDeviation = 7.516648189186454
                    ),
                    numberOfChildren = NormalIntDistribution(
                            average = 1,
                            minimum = 0,
                            maximum = 1,
                            mode = 1,
                            median = 0,
                            standardDeviation = 0.7071067811865476
                    ),
                    averageChild = JsonSizeLeafOverview(
                            name = "averageChild",
                            size = NormalIntDistribution(
                                    average = 10,
                                    minimum = 10,
                                    maximum = 10,
                                    mode = 10,
                                    median = 10,
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

        jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2) failsAnd { message ->

            assertThat(message).isEqualTo("Nodes do not match")
        }
    }

    @Test
    fun `it cannot sum JsonSizeObjects that have children with different key types`() {

        val node1 = JsonSizeObject(
                name = "A",
                size = 15,
                children = listOf(JsonSizeLeafNode(name = "A", size = 10))
        )
        val node2 = JsonSizeObject(
                name = "A",
                size = 15,
                children = listOf(JsonSizeObject(
                        name = "A",
                        size = 15,
                        children = listOf(JsonSizeLeafNode(name = "A", size = 10))))
        )

        jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2) failsAnd { message ->

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
                )
        )
        val node2 = JsonSizeArray(
                name = "A",
                size = 15,
                children = listOf(JsonSizeLeafNode(name = "B", size = 15))
        )

        jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2) failsAnd { message ->

            assertThat(message).isEqualTo("Nodes do not match")
        }
    }

    @Test
    @Ignore("TODO NOT A VALID TEST, DO WE NEED SOME FORM OF ARRAY CHILD TO PROHIBIT THIS SETUP FROM EVER HAPPENING?")
    fun `it cannot sum JsonSizeArrays that are shaped differently`() {

        val node1 = JsonSizeArray(
                name = "A",
                size = 15,
                children = listOf(JsonSizeLeafNode(name = "0", size = 10))
        )
        val node2 = JsonSizeArray(
                name = "A",
                size = 15,
                children = listOf(JsonSizeLeafNode(name = "1", size = 15))
        )

        jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2) failsAnd { message ->

            assertThat(message).isEqualTo("Nodes do not match")
        }
    }

    @Test
    fun `it can sum a list of simple JsonSizeLeafNodes`() {

        val node1 = JsonSizeLeafNode(name = "A", size = 10)
        val node2 = JsonSizeLeafNode(name = "A", size = 15)

        jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2) succeedsAnd {

            assertThat(it).isEqualTo(JsonSizeLeafOverview(name = "A", size = NormalIntDistribution(
                    average = 13,
                    minimum = 10,
                    maximum = 15,
                    mode = 15,
                    median = 12,
                    standardDeviation = 2.5495097567963922
            )))
        }
    }

    @Test
    fun `it can sum a list of JsonSizeObjects`() {

        val node1 = JsonSizeObject(
                name = "A",
                size = 15,
                children = listOf(JsonSizeLeafNode(name = "B", size = 10))
        )
        val node2 = JsonSizeObject(
                name = "A",
                size = 24,
                children = listOf(JsonSizeLeafNode(name = "B", size = 19))
        )

        jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2) succeedsAnd {

            assertThat(it).isEqualToComparingFieldByFieldRecursively(JsonSizeObjectOverview(
                    name = "A",
                    size = NormalIntDistribution(
                            average = 19,
                            minimum = 15,
                            maximum = 24,
                            mode = 24,
                            median = 19,
                            standardDeviation = 4.527692569068709
                    ),
                    children = listOf(JsonSizeObjectChild(
                            overview = JsonSizeLeafOverview(name = "B", size = NormalIntDistribution(
                                    average = 15,
                                    minimum = 10,
                                    maximum = 19,
                                    mode = 19,
                                    median = 14,
                                    standardDeviation = 4.527692569068709
                            )),
                            presence = NormalDoubleDistribution(
                                    average = 1.0,
                                    minimum = 1.0,
                                    maximum = 1.0,
                                    mode = 1.0,
                                    median = 1.0,
                                    standardDeviation = 0.0
                            )
                    ))
            ))
        }
    }

    @Test
    fun `it can sum a list of JsonSizeObjects with different keys`() {

        val node1 = JsonSizeObject(
                name = "A",
                size = 15,
                children = listOf(
                        JsonSizeLeafNode(name = "B", size = 10),
                        JsonSizeLeafNode(name = "C", size = 15)
                )
        )
        val node2 = JsonSizeObject(
                name = "A",
                size = 24,
                children = listOf(
                        JsonSizeLeafNode(name = "B", size = 19),
                        JsonSizeLeafNode(name = "D", size = 25)
                )
        )

        jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2) succeedsAnd {

            assertThat(it).isEqualToComparingFieldByFieldRecursively(JsonSizeObjectOverview(
                    name = "A",
                    size = NormalIntDistribution(
                            average = 19,
                            minimum = 15,
                            maximum = 24,
                            mode = 24,
                            median = 19,
                            standardDeviation = 4.527692569068709
                    ),
                    children = listOf(
                            JsonSizeObjectChild(
                                    overview = JsonSizeLeafOverview(name = "B", size = NormalIntDistribution(
                                            average = 15,
                                            minimum = 10,
                                            maximum = 19,
                                            mode = 19,
                                            median = 14,
                                            standardDeviation = 4.527692569068709
                                    )),
                                    presence = NormalDoubleDistribution(
                                            average = 1.0,
                                            minimum = 1.0,
                                            maximum = 1.0,
                                            mode = 1.0,
                                            median = 1.0,
                                            standardDeviation = 0.0
                                    )
                            ),
                            JsonSizeObjectChild(
                                    overview = JsonSizeLeafOverview(name = "C", size = NormalIntDistribution(
                                            average = 15,
                                            minimum = 15,
                                            maximum = 15,
                                            mode = 15,
                                            median = 15,
                                            standardDeviation = 0.0
                                    )),
                                    presence = NormalDoubleDistribution(
                                            average = 0.5,
                                            minimum = 0.0,
                                            maximum = 1.0,
                                            mode = 0.5,
                                            median = 1.0,
                                            standardDeviation = 0.5
                                    )
                            ),
                            JsonSizeObjectChild(
                                    overview = JsonSizeLeafOverview(name = "D", size = NormalIntDistribution(
                                            average = 25,
                                            minimum = 25,
                                            maximum = 25,
                                            mode = 25,
                                            median = 25,
                                            standardDeviation = 0.0
                                    )),
                                    presence = NormalDoubleDistribution(
                                            average = 0.5,
                                            minimum = 0.0,
                                            maximum = 1.0,
                                            mode = 0.5,
                                            median = 1.0,
                                            standardDeviation = 0.5
                                    )
                            ))
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

                                )
                        ),
                        JsonSizeObject(name = "1", size = 25,
                                children = listOf(
                                        JsonSizeLeafNode(name = "A", size = 6),
                                        JsonSizeLeafNode(name = "B", size = 8)
                                )
                        )
                )
        )

        val node2 = JsonSizeArray(
                name = "top",
                size = 63,
                children = listOf(
                        JsonSizeObject(name = "0", size = 25,
                                children = listOf(
                                        JsonSizeLeafNode(name = "A", size = 6),
                                        JsonSizeLeafNode(name = "B", size = 8)
                                )
                        ),
                        JsonSizeObject(name = "1", size = 35,
                                children = listOf(
                                        JsonSizeLeafNode(name = "A", size = 10),
                                        JsonSizeLeafNode(name = "B", size = 14)
                                )
                        ),
                        JsonSizeObject(name = "1", size = 35,
                                children = listOf(
                                        JsonSizeLeafNode(name = "A", size = 20),
                                        JsonSizeLeafNode(name = "B", size = 14)
                                )
                        )
                )
        )

        val result = jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2)

        result succeedsAnd {


            val jsonSizeArrayOverview = it as JsonSizeArrayOverview

            assertThat(jsonSizeArrayOverview.name).isEqualTo("top")

            assertThat(jsonSizeArrayOverview.size).isEqualTo(NormalIntDistribution(
                    average = 55,
                    minimum = 48,
                    maximum = 63,
                    mode = 63,
                    median = 55,
                    standardDeviation = 7.516648189186454
            ))

            assertThat(jsonSizeArrayOverview.averageChild).isEqualToComparingFieldByFieldRecursively(
                    JsonSizeObjectOverview(
                            name = "averageChild",
                            size = NormalIntDistribution(
                                    average = 28,
                                    minimum = 20,
                                    maximum = 35,
                                    mode = 35,
                                    median = 25,
                                    standardDeviation = 6.0
                            ),
                            children = listOf(
                                    JsonSizeObjectChild(
                                            overview = JsonSizeLeafOverview(name = "A", size = NormalIntDistribution(
                                                    average = 9,
                                                    minimum = 4,
                                                    maximum = 20,
                                                    mode = 6,
                                                    median = 6,
                                                    standardDeviation = 5.744562646538029
                                            )),
                                            presence = NormalDoubleDistribution(
                                                    average = 1.0,
                                                    minimum = 1.0,
                                                    maximum = 1.0,
                                                    mode = 1.0,
                                                    median = 1.0,
                                                    standardDeviation = 0.0
                                            )
                                    ),
                                    JsonSizeObjectChild(
                                            overview = JsonSizeLeafOverview(name = "B", size = NormalIntDistribution(
                                                    average = 9,
                                                    minimum = 5,
                                                    maximum = 14,
                                                    mode = 14,
                                                    median = 8,
                                                    standardDeviation = 3.687817782917155
                                            )),
                                            presence = NormalDoubleDistribution(
                                                    average = 1.0,
                                                    minimum = 1.0,
                                                    maximum = 1.0,
                                                    mode = 1.0,
                                                    median = 1.0,
                                                    standardDeviation = 0.0
                                            ))
                            )
                    )
            )

            assertThat(jsonSizeArrayOverview.numberOfChildren).isEqualTo(NormalIntDistribution(
                    average = 3,
                    minimum = 2,
                    maximum = 3,
                    mode = 3,
                    median = 2,
                    standardDeviation = 0.7071067811865476
            ))
        }
    }

    @Test
    fun `it can handle empty JsonSizeArrays`() {

        val node1 = JsonSizeArray(
                name = "top",
                size = 48,
                children = emptyList()
        )

        val node2 = JsonSizeArray(
                name = "top",
                size = 63,
                children = listOf(
                        JsonSizeObject(name = "0", size = 25,
                                children = listOf(
                                        JsonSizeLeafNode(name = "A", size = 6),
                                        JsonSizeLeafNode(name = "B", size = 8)
                                )
                        ),
                        JsonSizeObject(name = "1", size = 35,
                                children = listOf(
                                        JsonSizeLeafNode(name = "A", size = 10),
                                        JsonSizeLeafNode(name = "B", size = 14)
                                )
                        ),
                        JsonSizeObject(name = "1", size = 35,
                                children = listOf(
                                        JsonSizeLeafNode(name = "A", size = 20),
                                        JsonSizeLeafNode(name = "B", size = 14)
                                )
                        )
                )
        )

        val result = jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2)

        result succeedsAnd {

            assertThat(it is JsonSizeArrayOverview).isTrue()

            val jsonSizeArrayOverview = it as JsonSizeArrayOverview

            assertThat(jsonSizeArrayOverview.name).isEqualTo("top")

            assertThat(jsonSizeArrayOverview.size).isEqualTo(NormalIntDistribution(
                    average = 55,
                    minimum = 48,
                    maximum = 63,
                    mode = 63,
                    median = 55,
                    standardDeviation = 7.516648189186454
            ))

            assertThat(jsonSizeArrayOverview.averageChild).isEqualToComparingFieldByFieldRecursively(
                    JsonSizeObjectOverview(
                            name = "averageChild",
                            size = NormalIntDistribution(
                                    average = 31,
                                    minimum = 25,
                                    maximum = 35,
                                    mode = 35,
                                    median = 35,
                                    standardDeviation = 4.760952285695233
                            ),
                            children = listOf(
                                    JsonSizeObjectChild(
                                            overview = JsonSizeLeafOverview(name = "A", size = NormalIntDistribution(
                                                    average = 12,
                                                    minimum = 6,
                                                    maximum = 20,
                                                    mode = 20,
                                                    median = 10,
                                                    standardDeviation = 5.887840577551898
                                            )),
                                            presence = NormalDoubleDistribution(
                                                    average = 1.0,
                                                    minimum = 1.0,
                                                    maximum = 1.0,
                                                    mode = 1.0,
                                                    median = 1.0,
                                                    standardDeviation = 0.0
                                            )
                                    ),
                                    JsonSizeObjectChild(
                                            overview = JsonSizeLeafOverview(name = "B", size = NormalIntDistribution(
                                                    average = 12,
                                                    minimum = 8,
                                                    maximum = 14,
                                                    mode = 14,
                                                    median = 14,
                                                    standardDeviation = 2.8284271247461903
                                            )),
                                            presence = NormalDoubleDistribution(
                                                    average = 1.0,
                                                    minimum = 1.0,
                                                    maximum = 1.0,
                                                    mode = 1.0,
                                                    median = 1.0,
                                                    standardDeviation = 0.0
                                            ))
                            )
                    )
            )

            assertThat(jsonSizeArrayOverview.numberOfChildren).isEqualTo(NormalIntDistribution(
                    average = 1,
                    minimum = 0,
                    maximum = 3,
                    mode = 3,
                    median = 1,
                    standardDeviation = 1.5811388300841898
            ))
        }
    }

    private fun JsonSizeAnalyzer.generateJsonSizeOverview(vararg nodes: JsonSizeNode): SingleResult<String, JsonSizeOverview<Int>> {

        return generateJsonSizeOverview(nodes = nodes.asList())
    }
}