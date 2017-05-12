package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.Failure
import com.hcsc.de.claims.Result
import com.hcsc.de.claims.Success
import com.hcsc.de.claims.ceiling

class JsonSizeAverager {

    fun generateAverageJsonSizeNode(nodes: List<JsonSizeNode>): Result<String, JsonSizeNode> {

        return nodes.generateAveragedNode()
    }

    fun generateAverageJsonSizeNode(vararg nodes: JsonSizeNode): Result<String, JsonSizeNode> {

        return generateAverageJsonSizeNode(nodes = nodes.asList())
    }

    fun List<JsonSizeNode>.generateAveragedNode(): Result<String, JsonSizeNode> {

        val associatedByType = associateBy { it::class }

        return if (associatedByType.size > 1) {

            Failure(content = "Nodes are not the same type")

        } else {

            return if (map(JsonSizeNode::name).toSet().size > 1) {

                Failure(content = "Nodes do not match")

            } else {

                val type = associatedByType.keys.first()

                when (type.simpleName) {
                    "JsonSizeLeafNode" -> (this as List<JsonSizeLeafNode>).generateAveragedLeafNode()
                    "JsonSizeArray" -> (this as List<JsonSizeArray>).generateAveragedArrayNode()
                    "JsonSizeObject" -> (this as List<JsonSizeObject>).generateAveragedObjectNode()
                    else -> TODO()
                }
            }
        }
    }

    fun List<JsonSizeArray>.generateAveragedArrayNode(): Result<String, JsonSizeNode> {

        val averagedArrayChildrenResults = map { array -> array.childrenWithNormalizedNames.generateAveragedNode() }

        return if (averagedArrayChildrenResults.find { it is Failure } == null) {

            val averagedArrayChildResult = (averagedArrayChildrenResults as List<Success<String, JsonSizeNode>>)
                    .map(Success<String, JsonSizeNode>::content)
                    .generateAveragedNode()

            when (averagedArrayChildResult) {
                is Success -> Success<String, JsonSizeNode>(content = JsonSizeArrayAverage(
                        name = first().name,
                        size = map(JsonSizeArray::size).average().ceiling().toInt(),
                        averageChild = averagedArrayChildResult.content,
                        averageNumberOfChildren = map { it.children.size }.average().ceiling().toInt()
                        ))
                is Failure -> averagedArrayChildResult
            }
        } else {
            averagedArrayChildrenResults.first()
        }
    }

    val JsonSizeArray.childrenWithNormalizedNames: List<JsonSizeNode> get() {
        return children.map {
            when (it) {
                is JsonSizeLeafNode -> it.copy(name = "averageChild")
                is JsonSizeObject -> it.copy(name = "averageChild")
                is JsonSizeArray -> it.copy(name = "averageChild")
                is JsonSizeArrayAverage -> it.copy(name = "averageChild")
            }
        }
    }

    fun List<JsonSizeObject>.generateAveragedObjectNode(): Result<String, JsonSizeNode> {

        return if (map { it.children.map(JsonSizeNode::name) }.toSet().size > 1) {

            Failure(content = "Nodes do not match")
        } else {

            val averageChildResults = first().children.map { child ->

                val nodes = this.map { it.children.find { it.name == child.name } ?: throw RuntimeException("This should not happen") }

                nodes.generateAveragedNode()
            }

            val failures = averageChildResults.filter { it is Failure }

            if (failures.isNotEmpty()) {

                failures.first()
            } else {

                val averagedNode: JsonSizeNode = first().copy(
                        size = map(JsonSizeObject::size).average().ceiling().toInt(),
                        averageChildSize = map(JsonSizeObject::averageChildSize).average().ceiling().toInt(),
                        children = (averageChildResults as List<Success<String, JsonSizeNode>>).map { it.content }
                )

                Success(content = averagedNode)
            }
        }
    }

    fun List<JsonSizeLeafNode>.generateAveragedLeafNode(): Result<String, JsonSizeNode> {

        val averageLeafNode = first().copy(size = map(JsonSizeLeafNode::size).average().ceiling().toInt())

        return Success(content = averageLeafNode)
    }
}