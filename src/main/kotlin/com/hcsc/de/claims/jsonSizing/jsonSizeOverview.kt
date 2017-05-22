package com.hcsc.de.claims.jsonSizing

sealed class JsonSizeOverview {
    abstract val name: String
    abstract val size: Distribution
}

data class Distribution(
        val average: Int,
        val minimum: Int,
        val maximum: Int,
        val standardDeviation: Double
)

data class DoubleDistribution(
        val average: Double,
        val minimum: Double,
        val maximum: Double,
        val standardDeviation: Double
)

data class JsonSizeLeafOverview(
        override val name: String,
        override val size: Distribution
) : JsonSizeOverview()

data class JsonSizeObjectOverview(
        override val name: String,
        override val size: Distribution,
        val children: List<JsonSizeOverview>
) : JsonSizeOverview()

data class JsonSizeArrayOverview(
        override val name: String,
        override val size: Distribution,
        val averageChild: JsonSizeOverview,
        val numberOfChildren: Distribution
) : JsonSizeOverview()