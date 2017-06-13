package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.distributionFitting.DistributionGenerator
import com.hcsc.de.claims.distributions.*
import com.hcsc.de.claims.results.*
import kotlin.reflect.KClass

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

        println("Generating overview for new level")

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

        println("Generating leaf node")

        return sizeDistributionFromNode.map { sizeDistribution ->
            JsonSizeLeafOverview(
                    name = first().name,
                    size = sizeDistribution
            )
        }
    }

    private fun List<JsonSizeObject>.generateAveragedObjectNode(): Result<String, JsonSizeOverview<Int>> {

        println("Generating object")

        return collectAllChildrenNames()
                .map { generateJsonSizeObjectChild(name = it) }
                .traverse()
                .flatMap { createObjectOverview(children = it) }
    }

    private fun List<JsonSizeArray>.generateAveragedArrayNode(): Result<String, JsonSizeOverview<Int>> {

        println("Generating array")

        return flatMap { array -> array.childrenWithNormalizedNames }
                .generateOverview()
                .flatMap { generateJsonSizeArrayOverview(averageChild = it) }
    }

    private fun List<JsonSizeNode>.ensureNodesHaveSameName(): Result<String, Unit> =
            if (this.map(JsonSizeNode::name).toSet().size > 1) {
                Failure<String, Unit>(content = "Nodes do not match")
            } else {
                EMPTY_SUCCESS
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

    private fun List<JsonSizeObject>.createObjectOverview(
            children: List<JsonSizeObjectChild<Int>>
    ): Result<String, JsonSizeOverview<Int>> =
//            sizeDistributionFromNode.map { sizeDistribution ->
                Success(JsonSizeObjectOverview(
                        name = first().name,
                        size = EMPTY_DISTRIBUTION,
                        children = children
                ))
//            }

    private fun List<JsonSizeObject>.generateJsonSizeObjectChild(
            name: String
    ): Result<String, JsonSizeObjectChild<Int>> =
            findAllChildrenByName(name).let { childrenWithName ->

                childrenWithName.presenceProbability.flatMap { probability ->

                    childrenWithName
                            .filterNotNull()
                            .generateOverview()
                            .map { JsonSizeObjectChild(overview = it, presence = probability) }
                }
            }

    private fun List<JsonSizeArray>.generateJsonSizeArrayOverview(
            averageChild: JsonSizeOverview<Int>
    ): Result<String, JsonSizeOverview<Int>> =
    // TODO this should be optional maybe?
//            sizeDistributionFromNode.flatMap { sizeDistribution ->

                map { it.numberOfChildren }.sizeDistributionInt.map { it ->

                    JsonSizeArrayOverview(
                            name = first().name,
                            size = EMPTY_DISTRIBUTION,
                            averageChild = averageChild,
                            numberOfChildren = it
                    ) as JsonSizeOverview<Int>
                }
//            }

    private val EMPTY_DISTRIBUTION = NormalIntDistribution(average = 0, minimum = 0, maximum = 0, mode = 0, median = 0, standardDeviation = 0.0)

    private fun List<JsonSizeObject>.findAllChildrenByName(name: String): List<JsonSizeNode?> =
            map { it.children.find { it.name == name } }

    private fun List<JsonSizeObject>.collectAllChildrenNames(): Set<String> =
            flatMap { it.children.map { it.name } }.toSet()

    private val JsonSizeArray.childrenWithNormalizedNames: List<JsonSizeNode>
        get() = children.map {
            when (it) {
                is JsonSizeLeafNode -> it.copy(name = "averageChild")
                is JsonSizeObject -> it.copy(name = "averageChild")
                is JsonSizeArray -> it.copy(name = "averageChild")
                is JsonSizeEmpty -> it.copy(name = "averageChild")
            }
        }

    private val JsonSizeArray.numberOfChildren: Int get() = children.size

    private val List<JsonSizeNode?>.presenceProbability: Result<String, RatioProbability>
        get() = RatioProbability.create(presenceRatio)

    private val List<JsonSizeNode?>.presenceRatio: Double get() = filterNotNull().size.toDouble() / size

    private val List<JsonSizeNode>.sizeDistributionFromNode: Result<String, Distribution<Int>>
        get() = map(JsonSizeNode::size).sizeDistributionInt

    private val List<Int>.sizeDistributionInt: Result<String, Distribution<Int>>
        get() = map(Int::toDouble).sizeDistribution.map { it.asIntDistribution }

    private val List<Double>.sizeDistribution: Result<String, Distribution<Double>>
        get() = distributionGenerator.profile(this).map { it.distribution }

    private fun emptySizeArray(name: String) = JsonSizeArray(name = name, size = 0, children = emptyList())

    private fun emptySizeObject(name: String) = JsonSizeObject(name = name, size = 0, children = emptyList())

    private fun emptySizeLeafNode(name: String) = JsonSizeLeafNode(name = name, size = 0)
}
