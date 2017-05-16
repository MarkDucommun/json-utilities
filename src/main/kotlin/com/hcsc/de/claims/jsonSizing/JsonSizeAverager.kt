package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.*
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlin.reflect.KClass
import kotlin.reflect.full.cast

class JsonSizeAverager(private val scheduler: Scheduler = Schedulers.trampoline()) {

    fun generateJsonSizeOverview(nodes: List<JsonSizeNode>): SingleResult<String, JsonSizeOverview> {

        return nodes.generateOverview()
    }

    fun List<JsonSizeNode>.generateOverview(): SingleResult<String, JsonSizeOverview> {

        return ensureNodesHaveSameName()
                .flatMapSuccess { ensureNodesAreSameType() }
                .flatMapSuccess { type ->
                    when (type) {
                        "JsonSizeLeafNode" -> this.normalizeNodes<JsonSizeLeafNode>().generateAveragedLeafNode()
                        "JsonSizeArray" -> this.normalizeNodes<JsonSizeArray>().generateAveragedArrayNode()
                        "JsonSizeObject" -> this.normalizeNodes<JsonSizeObject>().generateAveragedObjectNode()
                        else -> Single.just(Success<String, JsonSizeOverview>(JsonSizeLeafOverview(name = first().name, size = Distribution(average = 0, minimum = 0, maximum = 0, standardDeviation = 0.0)))) as SingleResult<String, JsonSizeOverview>
                    }
                }
    }

    private inline fun <reified T : JsonSizeNode> List<JsonSizeNode>.normalizeNodes(): List<T> {
        return this.map { node: JsonSizeNode ->
            when (node) {
                is JsonSizeEmpty -> {
                    when (T::class) {
                        JsonSizeArray::class -> JsonSizeArray(name = node.name, size = 0, children = emptyList(), averageChildSize = 0) as T
                        JsonSizeObject::class -> JsonSizeObject(name = node.name, size = 0, children = emptyList(), averageChildSize = 0) as T
                        JsonSizeLeafNode::class -> JsonSizeLeafNode(name = node.name, size = 0) as T
                        else -> TODO()
                    }
                }
                else -> node as T
            }
        }
    }

    private fun List<JsonSizeLeafNode>.generateAveragedLeafNode(): SingleResult<String, JsonSizeOverview> {

        return doOnComputationThread {

            Success<String, JsonSizeOverview>(JsonSizeLeafOverview(
                    name = first().name,
                    size = sizeDistribution
            ))
        }
    }

    private fun List<JsonSizeObject>.generateAveragedObjectNode(): SingleResult<String, JsonSizeOverview> {

        return ensureNodesHaveSameFields().flatMapSuccess {

            first().children.map { child ->

                doOnComputationThreadAndFlatten {

                    val nodes = this.map { it.children.find { it.name == child.name } ?: throw RuntimeException("This should not happen") }

                    nodes.generateOverview()
                }
            }.concat().toList().map { results ->

                results.find { it is Failure }
                        ?.let { Failure<String, JsonSizeOverview>(content = (it as Failure).content) }
                        ?: Success<String, JsonSizeOverview>(JsonSizeObjectOverview(
                        name = first().name,
                        size = sizeDistribution,
                        children = (results as List<Success<String, JsonSizeOverview>>).map { it.content }
                ))
            }
        }
    }

    private fun List<JsonSizeArray>.generateAveragedArrayNode(): SingleResult<String, JsonSizeOverview> {

        return flatMap { array -> array.childrenWithNormalizedNames }.generateOverview().mapSuccess { averageChild ->

            Success<String, JsonSizeOverview>(content = JsonSizeArrayOverview(
                    name = first().name,
                    size = sizeDistribution,
                    averageChild = averageChild,
                    numberOfChildren = numberOfChildrenDistribution
            ))
        }
    }

    private fun List<JsonSizeNode>.ensureNodesHaveSameName(): SingleResult<String, Unit> {

        return doOnComputationThread {

            if (this.map(JsonSizeNode::name).toSet().size > 1) {
                Failure<String, Unit>(content = "Nodes do not match")
            } else {
                EMPTY_SUCCESS
            }
        }
    }

    private fun List<JsonSizeNode>.ensureNodesAreSameType(): SingleResult<String, String> {

        return doOnComputationThread {

            val associatedByType: Map<KClass<out JsonSizeNode>, List<JsonSizeNode>> = groupBy { it::class }
            val types = associatedByType.keys.map { it.simpleName }.filterNotNull()
            val numberOfTypes = associatedByType.keys.size

            when (numberOfTypes) {
                2 -> {
                    if (types.contains("JsonSizeEmpty")) {
                        Success<String, String>(content = types.filterNot { it == "JsonSizeEmpty"}.first())
                    } else {
                        Failure<String, String>(content = "Nodes are not the same type")
                    }
                }
                1 -> Success<String, String>(content = types.first())
                0 -> Failure<String, String>(content = "No associated types")
                else -> Failure<String, String>(content = "Nodes are not the same type")
            }
        }
    }

    private fun List<JsonSizeObject>.ensureNodesHaveSameFields(): SingleResult<String, Unit> {

        return doOnComputationThread {

            if (map { it.fields }.toSet().size > 1) {
                Failure<String, Unit>(content = "Nodes do not match")
            } else {
                EMPTY_SUCCESS
            }
        }
    }

    private val JsonSizeObject.fields get() = children.map(JsonSizeNode::name)

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

    private val List<JsonSizeNode>.sizeDistribution: Distribution get() = map(JsonSizeNode::size).distribution

    private val JsonSizeArray.numberOfChildren: Int get() = children.size

    private val List<JsonSizeArray>.numberOfChildrenDistribution: Distribution
        get() = map { it.numberOfChildren }.distribution

    private val List<Int>.distribution: Distribution get() {

        val average = averageInt()

        return Distribution(
                average = average,
                minimum = min() ?: 0,
                maximum = max() ?: 0,
                standardDeviation = map { member -> (member - average).square() }.average().sqrt()
        )
    }

    private fun <T> doOnComputationThread(fn: () -> T): Single<T> {
        return doOnThread(scheduler = scheduler, fn = fn)
    }

    private fun <T> doOnComputationThreadAndFlatten(fn: () -> Single<T>): Single<T> {
        return doOnThreadAndFlatten(scheduler = scheduler, fn = fn)
    }
}
