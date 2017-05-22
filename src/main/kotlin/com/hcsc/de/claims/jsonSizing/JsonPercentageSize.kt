package com.hcsc.de.claims.jsonSizing

sealed class JsonPercentageSize {
    abstract val name: String
    abstract val localPercent: PercentageDistribution
    abstract val globalPercent: PercentageDistribution
}

data class JsonPercentageSizeLeaf(
        override val name: String,
        override val localPercent: PercentageDistribution,
        override val globalPercent: PercentageDistribution
) : JsonPercentageSize()

data class JsonPercentageSizeObject(
        override val name: String,
        override val localPercent: PercentageDistribution,
        override val globalPercent: PercentageDistribution,
        val children: List<JsonPercentageSize>
) : JsonPercentageSize()

data class JsonPercentageSizeArray(
        override val name: String,
        override val localPercent: PercentageDistribution,
        override val globalPercent: PercentageDistribution,
        val averageChild: JsonPercentageSize,
        val numberOfChildren: Distribution
) : JsonPercentageSize()

data class PercentageDistribution(
        val average: Double,
        val minimum: Double,
        val maximum: Double
)