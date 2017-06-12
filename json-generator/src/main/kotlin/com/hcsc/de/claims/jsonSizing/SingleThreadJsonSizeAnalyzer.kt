package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.distributions.DistributionGenerator
import com.hcsc.de.claims.distributions.NormalIntDistribution
import com.hcsc.de.claims.distributions.RatioProbability
import com.hcsc.de.claims.distributions.asIntDistribution
import com.hcsc.de.claims.helpers.*
import kotlin.reflect.KClass

interface JsonSizeAnalyzer {

    fun generateJsonSizeOverview(nodes: List<JsonSizeNode>): Result<String, JsonSizeOverview<Int>>
}

class SingleThreadJsonSizeAnalyzer(
        analyzer: JsonSizeAnalyzer? = null,
        private val distributionGenerator: DistributionGenerator<Double>
) : JsonSizeAnalyzer {

    private val analyzer: JsonSizeAnalyzer = analyzer ?: this

    override fun generateJsonSizeOverview(nodes: List<JsonSizeNode>): Result<String, JsonSizeOverview<Int>> {

        return nodes.ensureNodesHaveSameName()
                .flatMap { nodes.ensureNodesAreSameType() }
                .flatMap { type ->
                    when (type) {
                        "JsonSizeObject" -> nodes.normalizeNodes<JsonSizeObject>().generateAveragedObjectNode()
                        "JsonSizeArray" -> nodes.normalizeNodes<JsonSizeArray>().generateAveragedArrayNode()
                        "JsonSizeLeafNode" -> nodes.normalizeNodes<JsonSizeLeafNode>().generateAveragedLeafNode()
                        else -> nodes.normalizeNodes<JsonSizeLeafNode>().generateAveragedLeafNode()
                    }
                }
    }

    fun List<JsonSizeNode>.generateOverview(): Result<String, JsonSizeOverview<Int>> {

        return analyzer.generateJsonSizeOverview(this)
    }

    private inline fun <reified T : JsonSizeNode> List<JsonSizeNode>.normalizeNodes(): List<T> {
        return this.map { node: JsonSizeNode ->
            when (node) {
                is JsonSizeEmpty -> {
                    when (T::class) {
                        JsonSizeArray::class -> emptySizeArray(name = node.name) as T
                        JsonSizeObject::class -> emptySizeObject(name = node.name) as T
                        JsonSizeLeafNode::class -> emptySizeLeafNode(name = node.name) as T
                        else -> emptySizeLeafNode(name = node.name) as T
                    }
                }
                else -> node as T
            }
        }
    }

    private fun List<JsonSizeLeafNode>.generateAveragedLeafNode(): Result<String, JsonSizeOverview<Int>> {

        return distributionGenerator.profile(map { it.size.toDouble() }).map {
            JsonSizeLeafOverview(
                    name = first().name,
                    size = it.distribution.asIntDistribution
            )
        }
    }

    private fun List<JsonSizeObject>.generateAveragedObjectNode(): Result<String, JsonSizeOverview<Int>> {

        return collectAllChildrenNames()
                .map { generateJsonSizeObjectChild(name = it) }
                .traverse()
                .map { JsonSizeObjectOverview(
                            name = first().name,
                            size = this.sizeDistribution,
                            children = it
                    )
                }
    }

    private fun List<JsonSizeObject>.generateJsonSizeObjectChild(name: String): Result<String, JsonSizeObjectChild<Int>> {

        return findAllChildrenByName(name).let { childrenWithName ->

            childrenWithName.presenceProbability.flatMap { probability ->

                childrenWithName
                        .filterNotNull()
                        .generateOverview()
                        .map { JsonSizeObjectChild(overview = it, presence = probability) }
            }
        }
    }

    private val List<JsonSizeNode?>.presenceProbability: Result<String, RatioProbability>
        get() = RatioProbability.create(presenceRatio)

    private val List<JsonSizeNode?>.presenceRatio: Double get() = filterNotNull().size.toDouble() / size

    private fun List<JsonSizeObject>.findAllChildrenByName(name: String): List<JsonSizeNode?> {
        return map { it.children.find { it.name == name } }
    }

    private fun List<JsonSizeArray>.generateAveragedArrayNode(): Result<String, JsonSizeOverview<Int>> {

        return flatMap { array -> array.childrenWithNormalizedNames }.generateOverview().map { averageChild ->

            JsonSizeArrayOverview(
                    name = first().name,
                    size = sizeDistribution,
                    averageChild = averageChild,
                    numberOfChildren = numberOfChildrenDistribution
            )
        }
    }

    private fun List<JsonSizeNode>.ensureNodesHaveSameName(): Result<String, Unit> {

        return if (this.map(JsonSizeNode::name).toSet().size > 1) {
            Failure<String, Unit>(content = "Nodes do not match")
        } else {
            EMPTY_SUCCESS
        }
    }

    private fun List<JsonSizeNode>.ensureNodesAreSameType(): Result<String, String> {

        val associatedByType: Map<KClass<out JsonSizeNode>, List<JsonSizeNode>> = groupBy { it::class }
        val types = associatedByType.keys.map { it.simpleName }.filterNotNull()
        val numberOfTypes = associatedByType.keys.size

        return when (numberOfTypes) {
            2 -> {
                if (types.contains("JsonSizeEmpty")) {
                    Success<String, String>(content = types.filterNot { it == "JsonSizeEmpty" }.first())
                } else {
                    Failure<String, String>(content = "Nodes are not the same type")
                }
            }
            1 -> Success<String, String>(content = types.first())
            0 -> Success<String, String>(content = "")
            else -> Failure<String, String>(content = "Nodes are not the same type")
        }
    }

    private fun List<JsonSizeObject>.collectAllChildrenNames(): Set<String> {
        return flatMap { it.children.map { it.name } }.toSet()
    }

    private val JsonSizeArray.childrenWithNormalizedNames: List<JsonSizeNode> get() {
        return children.map {
            when (it) {
                is JsonSizeLeafNode -> it.copy(name = "averageChild")
                is JsonSizeObject -> it.copy(name = "averageChild")
                is JsonSizeArray -> it.copy(name = "averageChild")
                is JsonSizeEmpty -> it.copy(name = "averageChild")
            }
        }
    }

    private val List<JsonSizeNode>.sizeDistribution: NormalIntDistribution
        get() = map(JsonSizeNode::size).distribution

    private val JsonSizeArray.numberOfChildren: Int get() = children.size

    private val List<JsonSizeArray>.numberOfChildrenDistribution: NormalIntDistribution
        get() = map { it.numberOfChildren }.distribution

    private val List<Int>.distribution: NormalIntDistribution get() {

        val average = averageInt()

        return NormalIntDistribution(
                average = average,
                minimum = min() ?: 0,
                maximum = max() ?: 0,
                mode = modeInt(),
                median = medianInt(),
                standardDeviation = map { member -> (member - average).square() }.average().sqrt()
        )
    }

    private fun emptySizeArray(name: String) = JsonSizeArray(name = name, size = 0, children = emptyList())

    private fun emptySizeObject(name: String) = JsonSizeObject(name = name, size = 0, children = emptyList())

    private fun emptySizeLeafNode(name: String) = JsonSizeLeafNode(name = name, size = 0)
}
