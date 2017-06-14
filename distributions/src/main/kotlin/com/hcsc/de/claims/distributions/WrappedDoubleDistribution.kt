package com.hcsc.de.claims.distributions

data class WrappedDoubleDistribution(
        private val doubleDistribution: Distribution<Double>
): Distribution<Int> {

    override fun random(): Int { return doubleDistribution.random().toInt() }

    override val average: Int get() = doubleDistribution.average.toInt()
    override val minimum: Int get() = doubleDistribution.minimum.toInt()
    override val maximum: Int get() = doubleDistribution.maximum.toInt()
    override val mode: Int get() = doubleDistribution.mode.toInt()
    override val median: Int get() = doubleDistribution.median.toInt()
}