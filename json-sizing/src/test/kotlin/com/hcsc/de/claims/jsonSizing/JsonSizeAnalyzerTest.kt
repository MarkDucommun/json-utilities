package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.distributions.generation.DistributionGenerator
import com.hcsc.de.claims.distributions.generation.DistributionProfile
import com.hcsc.de.claims.distributions.*
import com.hcsc.de.claims.distributions.generation.asIntDistribution
import com.hcsc.de.claims.results.*
import com.nhaarman.mockito_kotlin.*
import junit.framework.Assert.fail
import org.assertj.core.api.KotlinAssertions.assertThat
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

    val defaultDistribution: Distribution<Double> = mock()

    val mockDistributionGenerator: DistributionGenerator<Double, DistributionProfile<Double, Distribution<Double>>, Distribution<Double>> = mock {
        on { profile(any()) } doReturn Success(DistributionProfile(0.0, defaultDistribution))
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

        val expectedDistribution = mock<Distribution<Double>>()

        whenever(mockDistributionGenerator.profile(any()))
                .thenReturn(Success(DistributionProfile(0.0, expectedDistribution)))

        val node1 = JsonSizeObject(name = "A", size = 10, children = emptyList())
        val node2 = JsonSizeEmpty(name = "A")

        jsonSizeAnalyzer.generateJsonSizeOverview(node1, node2) succeedsAnd {

            mockGeneratorReceivedInAnyOrder(listOf(10.0, 0.0))

            assertThat(it).isEqualTo(JsonSizeObjectOverview(
                    name = "A",
                    size = expectedDistribution.asIntDistribution,
                    children = emptyList()
            ))
        }

    }

    @Test
    fun `it can sum JsonSizeArray and JsonSizeEmpty`() {

        val expectedDistribution = mock<Distribution<Double>>()

        whenever(mockDistributionGenerator.profile(any()))
                .thenReturn(Success(DistributionProfile(0.0, expectedDistribution)))

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

            mockGeneratorReceivedInAnyOrder(listOf(15.0, 0.0), listOf(1.0, 0.0))

            assertThat(it).isEqualToComparingFieldByFieldRecursively(JsonSizeArrayOverview(
                    name = "A",
                    size = expectedDistribution.asIntDistribution,
                    numberOfChildren = expectedDistribution.asIntDistribution,
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

        val expectedDistribution = mock<Distribution<Double>>()

        whenever(mockDistributionGenerator.profile(any()))
                .thenReturn(Success(DistributionProfile(0.0, expectedDistribution)))

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

            mockGeneratorReceivedInAnyOrder(listOf(15.0, 24.0))

            mockAnalyzerReceivedInAnyOrder(listOf(child1, child2))

            assertThat(it).isEqualToComparingFieldByFieldRecursively(JsonSizeObjectOverview(
                    name = "A",
                    size = expectedDistribution.asIntDistribution,
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
                    size = defaultDistribution.asIntDistribution,
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

    @Test
    fun `it can handle empty JsonSizeArrays`() {

        val childOverview = JsonSizeLeafOverview<Int>(name = "a", size = mock())

        whenever(mockJsonSizeAnalyzer.generateJsonSizeOverview(any()))
                .doReturn(Success<String, JsonSizeOverview<Int>>(childOverview))

        val expectedDistribution = mock<Distribution<Double>>()

        whenever(mockDistributionGenerator.profile(any()))
                .thenReturn(Success(DistributionProfile(0.0, expectedDistribution)))

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
                            size = expectedDistribution.asIntDistribution,
                            averageChild = childOverview,
                            numberOfChildren = expectedDistribution.asIntDistribution
                    )
            )
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

    private fun SingleThreadJsonSizeAnalyzer.generateJsonSizeOverview(vararg nodes: JsonSizeNode): Result<String, JsonSizeOverview<Int>> {

        return generateJsonSizeOverview(nodes = nodes.asList())
    }
}