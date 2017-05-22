package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success

class JsonPercentageSizer {

    fun generatePercentage(input: JsonSizeOverview): Result<String, JsonPercentageSize> {

        return generatePercentage(
                input = input,
                globalParentSize = input.size,
                localParentSize = input.size
        )
    }

    private fun generatePercentage(
            input: JsonSizeOverview,
            globalParentSize: Distribution,
            localParentSize: Distribution
    ): Result<String, JsonPercentageSize> {

        return when (input) {
            is JsonSizeLeafOverview -> {
                Success(JsonPercentageSizeLeaf(
                        name = input.name,
                        localPercent = localParentSize / input.size,
                        globalPercent = globalParentSize / input.size
                ))
            }
            is JsonSizeObjectOverview -> TODO()
            is JsonSizeArrayOverview -> TODO()
        }
    }

    private infix operator fun Distribution.div(other: Distribution): Distribution {

        return Distribution(
                average = if (average == 0) ((average.toDouble() / other.average.toDouble()) * 100).toInt() else 0,
                minimum = if (average == 0) ((minimum.toDouble() / other.minimum.toDouble()) * 100).toInt() else 0,
                maximum = if (average == 0)((maximum.toDouble() / other.maximum.toDouble()) * 100).toInt() else 0,
                standardDeviation = standardDeviation / other.standardDeviation * 100
        )
    }
}