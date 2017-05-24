package com.hcsc.de.claims.jsonSizing

sealed class JsonPercentageSize {
    abstract val name: String
    abstract val localPercent: NormalPercentageDistribution
    abstract val globalPercent: NormalPercentageDistribution
}

data class JsonPercentageSizeLeaf(
        override val name: String,
        override val localPercent: NormalPercentageDistribution,
        override val globalPercent: NormalPercentageDistribution
) : JsonPercentageSize()

data class JsonPercentageSizeObject(
        override val name: String,
        override val localPercent: NormalPercentageDistribution,
        override val globalPercent: NormalPercentageDistribution,
        val children: List<JsonPercentageSize>
) : JsonPercentageSize()

data class JsonPercentageSizeArray(
        override val name: String,
        override val localPercent: NormalPercentageDistribution,
        override val globalPercent: NormalPercentageDistribution,
        val averageChild: JsonPercentageSize,
        val numberOfChildren: NormalIntDistribution
) : JsonPercentageSize()

data class NormalPercentageDistribution(
        val average: Double,
        val minimum: Double,
        val maximum: Double
)