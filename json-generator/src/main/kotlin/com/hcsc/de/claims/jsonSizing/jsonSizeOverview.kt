package com.hcsc.de.claims.jsonSizing

sealed class JsonSizeOverview {
    abstract val name: String
    abstract val size: Distribution
}

sealed class Distribution

sealed class DoubleDistribution : Distribution()

sealed class IntDistribution : Distribution()

data class NormalIntDistribution(
        val average: Int,
        val minimum: Int,
        val maximum: Int,
        val standardDeviation: Double
) : IntDistribution()

data class NormalDoubleDistribution(
        val average: Double,
        val minimum: Double,
        val maximum: Double,
        val standardDeviation: Double
) : DoubleDistribution()

data class JsonSizeLeafOverview(
        override val name: String,
        override val size: Distribution
) : JsonSizeOverview()

data class JsonSizeObjectOverview(
        override val name: String,
        override val size: Distribution,
        val children: List<JsonSizeObjectChild>
) : JsonSizeOverview()

data class JsonSizeObjectChild(
        val overview: JsonSizeOverview,
        val presence: DoubleDistribution
)

data class JsonSizeArrayOverview(
        override val name: String,
        override val size: NormalIntDistribution,
        val averageChild: JsonSizeOverview,
        val numberOfChildren: NormalIntDistribution
) : JsonSizeOverview()