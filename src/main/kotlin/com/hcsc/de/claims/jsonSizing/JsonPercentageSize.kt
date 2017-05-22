package com.hcsc.de.claims.jsonSizing

sealed class JsonPercentageSize {
    abstract val name: String
    abstract val localPercent: Distribution
    abstract val globalPercent: Distribution
}

data class JsonPercentageSizeLeaf(
        override val name: String,
        override val localPercent: Distribution,
        override val globalPercent: Distribution
) : JsonPercentageSize()

data class JsonPercentageSizeObject(
        override val name: String,
        override val localPercent: Distribution,
        override val globalPercent: Distribution,
        val children: List<JsonPercentageSize>
) : JsonPercentageSize()

data class JsonPercentageSizeArray(
        override val name: String,
        override val localPercent: Distribution,
        override val globalPercent: Distribution,
        val averageChild: JsonPercentageSize,
        val numberOfChildren: Distribution
) : JsonPercentageSize()