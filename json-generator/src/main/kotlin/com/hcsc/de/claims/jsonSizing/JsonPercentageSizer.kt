package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.helpers.*

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
                        localPercent = input.size / localParentSize,
                        globalPercent = input.size / globalParentSize
                ))
            }
            is JsonSizeObjectOverview -> {
                return input.children.map { (overview) ->

                    generatePercentage(overview, globalParentSize, input.size)
                }.traverse().flatMap { childPercentages: List<JsonPercentageSize> ->

                    Success<String, JsonPercentageSize>(JsonPercentageSizeObject(
                            name = input.name,
                            localPercent = input.size / localParentSize,
                            globalPercent = input.size / globalParentSize,
                            children = childPercentages
                    ))
                }
            }
            is JsonSizeArrayOverview -> {
                return generatePercentage(input.averageChild, globalParentSize, input.size)
                        .flatMap { averageChild ->
                            Success<String, JsonPercentageSize>(JsonPercentageSizeArray(
                                    name = input.name,
                                    localPercent = input.size / localParentSize,
                                    globalPercent = input.size / globalParentSize,
                                    averageChild = averageChild,
                                    numberOfChildren = input.numberOfChildren
                            ))
                        }
            }
        }
    }

    private infix operator fun Distribution.div(other: Distribution): NormalPercentageDistribution {

        // TODO fix me so that I have average, min and max on distribution
        this as NormalIntDistribution
        other as NormalIntDistribution

        return NormalPercentageDistribution(
                average = average safePercentage other.average,
                minimum = minimum safePercentage other.minimum,
                maximum = maximum safePercentage other.maximum
        )
    }

    private infix fun Int.safePercentage(other: Int): Double {
        return if (other == 0) 0.0 else (this.toDouble() / other.toDouble()) * 100
    }

    private infix fun Double.safePercentage(other: Double): Double {
        return if (other == 0.0) 0.0 else this / other * 100
    }
}