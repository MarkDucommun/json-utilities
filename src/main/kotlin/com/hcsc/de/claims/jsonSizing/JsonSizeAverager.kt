package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.*
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.schedulers.Schedulers

class JsonSizeAverager {

    fun generateAverageJsonSizeNode(nodes: List<JsonSizeNode>): SingleResult<String, JsonSizeNode> {

        return nodes.generateAveragedNode()
    }

    fun generateAverageJsonSizeNode(vararg nodes: JsonSizeNode): SingleResult<String, JsonSizeNode> {

        return generateAverageJsonSizeNode(nodes = nodes.asList())
    }

    fun List<JsonSizeNode>.generateAveragedNode(): SingleResult<String, JsonSizeNode> {

        return ensureNodesHaveSameName().flatMapSuccess {

            ensureNodesAreSameType()

        }.flatMapSuccess { type ->

            when (type) {
                "JsonSizeLeafNode" -> (this as List<JsonSizeLeafNode>).generateAveragedLeafNode()
                "JsonSizeArray" -> (this as List<JsonSizeArray>).generateAveragedArrayNode()
                "JsonSizeObject" -> (this as List<JsonSizeObject>).generateAveragedObjectNode()
                else -> TODO()
            }
        }
    }

    private fun List<JsonSizeNode>.ensureNodesHaveSameName(): SingleResult<String, List<JsonSizeNode>> {

        return Single.just(Unit).subscribeOn(Schedulers.computation()).map {

            if (this.map(JsonSizeNode::name).toSet().size > 1) {

                Failure<String, List<JsonSizeNode>>(content = "Nodes do not match")
            } else {
                Success<String, List<JsonSizeNode>>(content = this)
            }
        }
    }

    private fun List<JsonSizeNode>.ensureNodesAreSameType(): SingleResult<String, String> {

        return Single.just(this).subscribeOn(Schedulers.computation()).map {

            val associatedByType = associateBy { it::class }

            if (associatedByType.size > 1) {

                Failure<String, String>(content = "Nodes are not the same type")

            } else {

                Success<String, String>(content = associatedByType.keys.first().simpleName ?: "")
            }
        }
    }

    private fun List<JsonSizeObject>.ensureNodesHaveSameFields(): SingleResult<String, Unit> {

        return Single.just(this).subscribeOn(Schedulers.computation()).map {

            if (map { it.fields }.toSet().size > 1) {
                Failure<String, Unit>(content = "Nodes do not match")
            } else {
                EMPTY_SUCCESS
            }
        }
    }

    private val JsonSizeObject.fields get() = children.map(JsonSizeNode::name)

    private fun List<JsonSizeLeafNode>.generateAveragedLeafNode(): SingleResult<String, JsonSizeNode> {

        return Single.just(Unit).subscribeOn(Schedulers.computation()).map {

            val averageLeafNode = first().copy(size = average(JsonSizeLeafNode::size))

            Success<String, JsonSizeNode>(content = averageLeafNode)
        }
    }

    private fun <T> List<T>.average(fn: T.() -> Int): Int = map(fn).averageInt()

    private fun List<Int>.averageInt() = average().ceilingOnEven().toInt()

    private fun List<JsonSizeObject>.generateAveragedObjectNode(): SingleResult<String, JsonSizeNode> {

        return ensureNodesHaveSameFields().flatMapSuccess {

            first().children.map { child ->

                Single.just(Unit).subscribeOn(Schedulers.computation()).flatMap {

                    val nodes = this.map { it.children.find { it.name == child.name } ?: throw RuntimeException("This should not happen") }

                    nodes.generateAveragedNode()
                }
            }.concat().toList().map { results ->

                results.find { it is Failure } ?: Success<String, JsonSizeNode>(content = first().copy(
                        size = average(JsonSizeObject::size),
                        averageChildSize = average(JsonSizeObject::averageChildSize),
                        children = (results as List<Success<String, JsonSizeNode>>).map { it.content }
                ))
            }
        }
    }

    private fun <failureType, successType> List<SingleResult<failureType, successType>>.concat() = Single.concat(this)

    private fun List<JsonSizeArray>.generateAveragedArrayNode(): SingleResult<String, JsonSizeNode> {

        return map { array -> array.childrenWithNormalizedNames.generateAveragedNode() }.concat().toList().flatMap { results ->

            results
                    .find { it is Failure }
                    ?.let { Single.just(Failure<String, JsonSizeNode>(content = (it as Failure).content)) }
                    ?: {

                val successes = results as List<Success<String, JsonSizeNode>>

                val averageChildren = successes.map(Success<String, JsonSizeNode>::content)

                averageChildren.generateAveragedNode().flatMap { averagedArrayChildResult ->

                    when (averagedArrayChildResult) {
                        is Success -> {
                            Single.just(Success<String, JsonSizeNode>(content = JsonSizeArrayAverage(
                                    name = first().name,
                                    size = average(JsonSizeArray::size),
                                    averageChild = averagedArrayChildResult.content,
                                    averageNumberOfChildren = map { it.children.size }.averageInt()
                            )))
                        }
                        is Failure -> Single.just(Failure<String, JsonSizeNode>(content = averagedArrayChildResult.content))
                    }
                }
            }()
        }
    }

    private val JsonSizeArray.childrenWithNormalizedNames: List<JsonSizeNode> get() {
        return children.map {
            when (it) {
                is JsonSizeLeafNode -> it.copy(name = "averageChild")
                is JsonSizeObject -> it.copy(name = "averageChild")
                is JsonSizeArray -> it.copy(name = "averageChild")
                is JsonSizeArrayAverage -> it.copy(name = "averageChild")
            }
        }
    }
}