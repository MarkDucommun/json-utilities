package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.helpers.Result

interface Distribution<out numberType: Number> : Randomable<numberType> {
    val average: numberType
    val minimum: numberType
    val maximum: numberType
    val mode: numberType
    val median: numberType
}

val List<Int>.distribution: Distribution<Int> get() {

    return this.normalIntdistribution
}

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

val Distribution<Double>.asIntDistribution: Distribution<Int> get() = WrappedDoubleDistribution(this)
