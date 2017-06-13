package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.distributions.Distribution

sealed class JsonPercentageSize {
    abstract val name: String
    abstract val localPercent: com.hcsc.de.claims.jsonSizing.PercentageDistribution
    abstract val globalPercent: com.hcsc.de.claims.jsonSizing.PercentageDistribution
}

data class JsonPercentageSizeLeaf(
        override val name: String,
        override val localPercent: com.hcsc.de.claims.jsonSizing.PercentageDistribution,
        override val globalPercent: com.hcsc.de.claims.jsonSizing.PercentageDistribution
) : com.hcsc.de.claims.jsonSizing.JsonPercentageSize()

data class JsonPercentageSizeObject(
        override val name: String,
        override val localPercent: com.hcsc.de.claims.jsonSizing.PercentageDistribution,
        override val globalPercent: com.hcsc.de.claims.jsonSizing.PercentageDistribution,
        val children: List<com.hcsc.de.claims.jsonSizing.JsonPercentageSize>
) : com.hcsc.de.claims.jsonSizing.JsonPercentageSize()

data class JsonPercentageSizeArray<numberType: Number>(
        override val name: String,
        override val localPercent: com.hcsc.de.claims.jsonSizing.PercentageDistribution,
        override val globalPercent: com.hcsc.de.claims.jsonSizing.PercentageDistribution,
        val averageChild: com.hcsc.de.claims.jsonSizing.JsonPercentageSize,
        val numberOfChildren: Distribution<numberType>
) : com.hcsc.de.claims.jsonSizing.JsonPercentageSize()

data class PercentageDistribution(
        val average: Double,
        val minimum: Double,
        val maximum: Double
)