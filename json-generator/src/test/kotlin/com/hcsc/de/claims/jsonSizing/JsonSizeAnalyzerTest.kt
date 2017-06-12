package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.distributions.*
import com.hcsc.de.claims.failsAnd
import com.hcsc.de.claims.failsWithMessage
import com.hcsc.de.claims.get
import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.succeedsAnd
import com.nhaarman.mockito_kotlin.*
import junit.framework.Assert.fail
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Ignore
import org.junit.Test

class JsonSizeAnalyzerTest {

    val defaultSizeOverview = JsonSizeLeafOverview<Int>(
            name = "",
            size = mock()
    )
    val defaultOverviewResult: Success<String, JsonSizeOverview<Int>> = Success(defaultSizeOverview)

    val mockJsonSizeAnalyzer: JsonSizeAnalyzer = mock {
        on { generateJsonSizeOverview(any()) } doReturn defaultOverviewResult
    }

    val mockDistributionGenerator: DistributionGenerator<Double> = mock {
        on { profile(any()) } doReturn Success(DistributionProfile(0.0, mock()))
    }

    val jsonSizeAnalyzer = SingleThreadJsonSizeAnalyzer(
            analyzer = mockJsonSizeAnalyzer,
            distributionGenerator = mockDistributionGenerator
    )

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

        val expectedDistribution = mock<Distribution<Double>>()

        whenever(mockDistributionGenerator.profile(any()))
                .thenReturn(Success(DistributionProfile(0.0, expectedDistribution)))

        val node1 = JsonSizeLeafNode(name = "A", size = 10)
        val node2 = JsonSizeEmpty(name = "A")

        jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2) succeedsAnd {

            mockGeneratorReceivedInAnyOrder(listOf(10.0, 0.0))

            assertThat(it).isEqualTo(JsonSizeLeafOverview(
                    name = "A",
                    size = expectedDistribution.asIntDistribution
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

        val childNode = JsonSizeLeafNode(name = "a", size = 1)

        val childOverview = JsonSizeLeafOverview<Int>(name = "a", size = mock())

        whenever(mockJsonSizeAnalyzer.generateJsonSizeOverview(any()))
                .doReturn(Success<String, JsonSizeOverview<Int>>(childOverview))

        val node1 = JsonSizeEmpty(name = "A")
        val node2 = JsonSizeArray(
                name = "A",
                size = 15,
                children = listOf(childNode)
        )

        jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2) succeedsAnd {

            val normalizedChild = childNode.copy(name = "averageChild")

            mockAnalyzerReceivedInAnyOrder(listOf(normalizedChild))

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
                    averageChild = childOverview
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
    fun `it fails if the delegated size analyzer fails`() {

        whenever(mockJsonSizeAnalyzer.generateJsonSizeOverview(any()))
                .doReturn(Failure("I failed!"))

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

        jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2) failsWithMessage "I failed!"
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

        val expectedDistribution = mock<Distribution<Double>>()

        whenever(mockDistributionGenerator.profile(any()))
                .thenReturn(Success(DistributionProfile(0.0, expectedDistribution)))

        val node1 = JsonSizeLeafNode(name = "A", size = 10)
        val node2 = JsonSizeLeafNode(name = "A", size = 15)

        jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2) succeedsAnd {

            mockGeneratorReceivedInAnyOrder(listOf(10.0, 15.0))

            assertThat(it).isEqualTo(JsonSizeLeafOverview(
                    name = "A",
                    size = expectedDistribution.asIntDistribution
            ))
        }
    }

    @Test
    fun `it can sum a list of JsonSizeObjects`() {

        val childOverview = JsonSizeLeafOverview<Int>(name = "a", size = mock())

        whenever(mockJsonSizeAnalyzer.generateJsonSizeOverview(any()))
                .doReturn(Success<String, JsonSizeOverview<Int>>(childOverview))

        val child1 = JsonSizeLeafNode(name = "B", size = 10)
        val child2 = JsonSizeLeafNode(name = "B", size = 19)

        val node1 = JsonSizeObject(
                name = "A",
                size = 15,
                children = listOf(child1)
        )
        val node2 = JsonSizeObject(
                name = "A",
                size = 24,
                children = listOf(child2)
        )

        val expectedProbability = RatioProbability.create(1.0).get

        jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2) succeedsAnd {

            mockAnalyzerReceivedInAnyOrder(listOf(child1, child2))

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
                            overview = childOverview,
                            presence = expectedProbability
                    ))
            ))
        }
    }

    @Test
    fun `it can sum a list of JsonSizeObjects with different keys`() {

        val childOverview = JsonSizeLeafOverview<Int>(name = "a", size = mock())

        whenever(mockJsonSizeAnalyzer.generateJsonSizeOverview(any()))
                .doReturn(Success<String, JsonSizeOverview<Int>>(childOverview))

        val bChild1 = JsonSizeLeafNode(name = "B", size = 10)
        val bChild2 = JsonSizeLeafNode(name = "B", size = 19)
        val cChild = JsonSizeLeafNode(name = "C", size = 15)
        val dChild = JsonSizeLeafNode(name = "D", size = 25)

        val node1 = JsonSizeObject(
                name = "A",
                size = 15,
                children = listOf(
                        bChild1,
                        cChild
                )
        )
        val node2 = JsonSizeObject(
                name = "A",
                size = 24,
                children = listOf(
                        bChild2,
                        dChild
                )
        )

        val fullProbability = RatioProbability.create(1.0).get
        val halfProbability = RatioProbability.create(0.5).get

        jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2) succeedsAnd {

            mockAnalyzerReceivedInAnyOrder(listOf(bChild1, bChild2), listOf(cChild), listOf(dChild))

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
                                    overview = childOverview,
                                    presence = fullProbability
                            ),
                            JsonSizeObjectChild(
                                    overview = childOverview,
                                    presence = halfProbability
                            ),
                            JsonSizeObjectChild(
                                    overview = childOverview,
                                    presence = halfProbability
                            ))
            ))
        }
    }

    fun mockAnalyzerReceivedInAnyOrder(vararg children: List<JsonSizeNode>) {

        argumentCaptor<List<JsonSizeNode>>().apply {

            verify(mockJsonSizeAnalyzer, times(children.size)).generateJsonSizeOverview(capture())

            children.forEach { childList ->
                if (allValues.map {
                    try {
                        assertThat(it).containsExactlyInAnyOrder(*childList.toTypedArray())
                        true
                    } catch (e: AssertionError) {
                        false
                    }
                }.filter { it }.size != 1) fail("Was not called with: $childList")
            }
        }
    }

    fun mockGeneratorReceivedInAnyOrder(vararg children: List<Double>) {

        argumentCaptor<List<Double>>().apply {

            verify(mockDistributionGenerator, times(children.size)).profile(capture())

            children.forEach { childList ->
                if (allValues.map {
                    try {
                        assertThat(it).containsExactlyInAnyOrder(*childList.toTypedArray())
                        true
                    } catch (e: AssertionError) {
                        false
                    }
                }.filter { it }.size != 1) fail("Was not called with: $childList")
            }
        }
    }

    @Test
    fun `it can handle empty JsonSizeArrays`() {

        val childOverview = JsonSizeLeafOverview<Int>(name = "a", size = mock())

        whenever(mockJsonSizeAnalyzer.generateJsonSizeOverview(any()))
                .doReturn(Success<String, JsonSizeOverview<Int>>(childOverview))

        val node1 = JsonSizeArray(
                name = "top",
                size = 48,
                children = emptyList()
        )

        val child1 = JsonSizeLeafNode(name = "A", size = 6)
        val child2 = JsonSizeLeafNode(name = "A", size = 10)
        val child3 = JsonSizeLeafNode(name = "A", size = 20)

        val node2 = JsonSizeArray(
                name = "top",
                size = 63,
                children = listOf(
                        child1,
                        child2,
                        child3
                )
        )

        jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2) succeedsAnd {

            val normalizedChild = listOf(child1, child2, child3).map { it.copy(name = "averageChild") }

            mockAnalyzerReceivedInAnyOrder(normalizedChild)

            assertThat(it).isEqualToComparingFieldByFieldRecursively(
                    JsonSizeArrayOverview(
                            name = "top",
                            size = NormalIntDistribution(
                                    average = 55,
                                    minimum = 48,
                                    maximum = 63,
                                    mode = 63,
                                    median = 55,
                                    standardDeviation = 7.516648189186454
                            ),
                            averageChild = childOverview,
                            numberOfChildren = NormalIntDistribution(
                                    average = 1,
                                    minimum = 0,
                                    maximum = 3,
                                    mode = 3,
                                    median = 1,
                                    standardDeviation = 1.5811388300841898
                            )
                    )
            )
        }
    }

    private fun SingleThreadJsonSizeAnalyzer.generateJsonSizeOverview(vararg nodes: JsonSizeNode): Result<String, JsonSizeOverview<Int>> {

        return generateJsonSizeOverview(nodes = nodes.asList())
    }
}