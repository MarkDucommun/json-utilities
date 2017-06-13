package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.distributions.Distribution
import com.hcsc.de.claims.results.flatMap
import com.hcsc.de.claims.results.traverse

class JsonPercentageSizer {

    fun <numberType: Number> generatePercentage(input: JsonSizeOverview<numberType>): com.hcsc.de.claims.results.Result<String, JsonPercentageSize> {

        return generatePercentage(
                input = input,
                globalParentSize = input.size,
                localParentSize = input.size
        )
    }

    private fun <numberType: Number> generatePercentage(
            input: JsonSizeOverview<numberType>,
            globalParentSize: Distribution<numberType>,
            localParentSize: Distribution<numberType>
    ): com.hcsc.de.claims.results.Result<String, JsonPercentageSize> {

        return when (input) {
            is JsonSizeLeafOverview -> {
                com.hcsc.de.claims.results.Success(JsonPercentageSizeLeaf(
                        name = input.name,
                        localPercent = input.size / localParentSize,
                        globalPercent = input.size / globalParentSize
                ))
            }
            is JsonSizeObjectOverview -> {
                return input.children.map { (overview) ->

                    generatePercentage(overview, globalParentSize, input.size)
                }.traverse().flatMap { childPercentages: List<JsonPercentageSize> ->

                    com.hcsc.de.claims.results.Success<String, JsonPercentageSize>(JsonPercentageSizeObject(
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
                            com.hcsc.de.claims.results.Success<String, JsonPercentageSize>(JsonPercentageSizeArray(
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

    private infix operator fun <numberType: Number> Distribution<numberType>.div(other: Distribution<numberType>): PercentageDistribution {

        return PercentageDistribution(
                average = average safePercentage other.average,
                minimum = minimum safePercentage other.minimum,
                maximum = maximum safePercentage other.maximum
        )
    }

    private infix fun <numberType : Number> numberType.safePercentage(other: numberType) : Double {
        return if (other == 0) 0.0 else (this.toDouble() / other.toDouble()) * 100
    }
}