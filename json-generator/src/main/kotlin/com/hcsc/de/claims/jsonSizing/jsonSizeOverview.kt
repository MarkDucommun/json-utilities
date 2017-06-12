package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.distributions.Distribution
import com.hcsc.de.claims.distributions.Probability

sealed class JsonSizeOverview<out numberType: Number> {
    abstract val name: String
    abstract val size: Distribution<numberType>
}

data class JsonSizeLeafOverview<out numberType: Number>(
        override val name: String,
        override val size: Distribution<numberType>
) : JsonSizeOverview<numberType>()

data class JsonSizeObjectOverview<out numberType: Number>(
        override val name: String,
        override val size: Distribution<numberType>,
        val children: List<JsonSizeObjectChild<numberType>>
) : JsonSizeOverview<numberType>()

data class JsonSizeObjectChild<out numberType: Number>(
        val overview: JsonSizeOverview<numberType>,
        val presence: Probability
)

data class JsonSizeArrayOverview<out numberType: Number>(
        override val name: String,
        override val size: Distribution<numberType>,
        val averageChild: JsonSizeOverview<numberType>,
        val numberOfChildren: Distribution<numberType>
) : JsonSizeOverview<numberType>()