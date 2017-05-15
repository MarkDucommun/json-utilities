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

            val averageLeafNode = first().copy(size = map(JsonSizeLeafNode::size).average().ceiling().toInt())

            Success<String, JsonSizeNode>(content = averageLeafNode)
        }
    }

    private fun List<JsonSizeObject>.generateAveragedObjectNode(): SingleResult<String, JsonSizeNode> {

        return ensureNodesHaveSameFields().flatMapSuccess {

            first().children.map { child ->

                Single.just(Unit).subscribeOn(Schedulers.computation()).flatMap {

                    val nodes = this.map { it.children.find { it.name == child.name } ?: throw RuntimeException("This should not happen") }

                    nodes.generateAveragedNode()
                }
            }.concat().toList().map { results ->

                results.find { it is Failure } ?: Success<String, JsonSizeNode>(content = first().copy(
                        size = map(JsonSizeObject::size).average().ceiling().toInt(),
                        averageChildSize = map(JsonSizeObject::averageChildSize).average().ceiling().toInt(),
                        children = (results as List<Success<String, JsonSizeNode>>).map { it.content }
                ))
            }
        }
    }

    private fun <failureType, successType> List<SingleResult<failureType, successType>>.concat() = Single.concat(this)

    private fun List<JsonSizeArray>.generateAveragedArrayNode(): SingleResult<String, JsonSizeNode> {

        map { array -> array.childrenWithNormalizedNames.generateAveragedNode() }.concat().toList().map { results: MutableList<Result<String, JsonSizeNode>> ->

//            if (results.find { it is Failure } == null)
        }

        TODO()
//
//        return if (averagedArrayChildrenResults.find { it is Failure } == null) {
//
//            val averagedArrayChildResult = (averagedArrayChildrenResults as List<Success<String, JsonSizeNode>>)
//                    .map(Success<String, JsonSizeNode>::content)
//                    .generateAveragedNode()
//
//            when (averagedArrayChildResult) {
//                is Success -> Success<String, JsonSizeNode>(content = JsonSizeArrayAverage(
//                        name = first().name,
//                        size = map(JsonSizeArray::size).average().ceiling().toInt(),
//                        averageChild = averagedArrayChildResult.content,
//                        averageNumberOfChildren = map { it.children.size }.average().ceiling().toInt()
//                        ))
//                is Failure -> averagedArrayChildResult
//            }
//        } else {
//            averagedArrayChildrenResults.first()
//        }
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