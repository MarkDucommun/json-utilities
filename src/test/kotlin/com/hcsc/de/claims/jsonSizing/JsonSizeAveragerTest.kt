package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.failsAnd
import com.hcsc.de.claims.succeedsAnd
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Ignore
import org.junit.Test

class JsonSizeAveragerTest {

    val jsonSizeAverager = JsonSizeAverager()

    @Test
    fun `it cannot sum JsonSizeNodes that are different types`() {

        val node1 = JsonSizeLeafNode(name = "A", size = 10)
        val node2 = JsonSizeObject(name = "B", size = 15, children = emptyList(), averageChildSize = 0)

        jsonSizeAverager.generateAverageJsonSizeNode(node1, node2) failsAnd { message ->

            assertThat(message).isEqualTo("Nodes are not the same type")
        }
    }

    @Test
    fun `it cannot sum JsonSizeNodes that are named differently`() {

        val node1 = JsonSizeLeafNode(name = "A", size = 10)
        val node2 = JsonSizeLeafNode(name = "B", size = 15)

        jsonSizeAverager.generateAverageJsonSizeNode(node1, node2) failsAnd { message ->

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

        jsonSizeAverager.generateAverageJsonSizeNode(node1, node2) failsAnd { message ->

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
                children = listOf(JsonSizeObject(name = "A", size = 15, children = emptyList(), averageChildSize = 0)),
                averageChildSize = 10
        )

        jsonSizeAverager.generateAverageJsonSizeNode(node1, node2) failsAnd { message ->

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

        jsonSizeAverager.generateAverageJsonSizeNode(node1, node2) failsAnd { message ->

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

        jsonSizeAverager.generateAverageJsonSizeNode(node1, node2) failsAnd { message ->

            assertThat(message).isEqualTo("Nodes do not match")
        }
    }

    @Test
    fun `it can sum a list of simple JsonSizeLeafNodes`() {

        val node1 = JsonSizeLeafNode(name = "A", size = 10)
        val node2 = JsonSizeLeafNode(name = "A", size = 15)

        jsonSizeAverager.generateAverageJsonSizeNode(node1, node2) succeedsAnd { averageNode ->

            assertThat(averageNode).isEqualTo(JsonSizeLeafNode(name = "A", size = 13))
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

        jsonSizeAverager.generateAverageJsonSizeNode(node1, node2) succeedsAnd { averageNode ->

            assertThat(averageNode).isEqualTo(JsonSizeObject(
                    name = "A",
                    size = 20,
                    children = listOf(JsonSizeLeafNode(name = "B", size = 15)),
                    averageChildSize = 15
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

        val result = jsonSizeAverager.generateAverageJsonSizeNode(node1, node2)

        result succeedsAnd { averagedNode ->

            assertThat(averagedNode).isEqualTo(
                    JsonSizeArrayAverage(
                            name = "top",
                            size = 56,
                            averageChild = JsonSizeObject(
                                    name = "averageChild",
                                    size = 28,
                                    children = listOf(
                                            JsonSizeLeafNode(name = "A", size = 9),
                                            JsonSizeLeafNode(name = "B", size = 10)
                                    ),
                                    averageChildSize = 9
                            ),
                            averageNumberOfChildren = 3
                    )
            )
        }
    }
}