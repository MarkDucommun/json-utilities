package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.helpers.Result

interface DistributionGenerator<numberType: Number> {

    fun profile(list: List<numberType>): Result<String, DistributionProfile<numberType>>
}

data class DistributionProfile<out numberType: Number>(
        val pValue: Double,
        val distribution: Distribution<numberType>
)

data class WrappedIntDistribution(
        val doubleDistribution: Distribution<Double>
) : Distribution<Int> {

    override val average: Int = doubleDistribution.average.toInt()
    override val minimum: Int = doubleDistribution.minimum.toInt()
    override val maximum: Int = doubleDistribution.maximum.toInt()
    override val mode: Int = doubleDistribution.mode.toInt()
    override val median: Int = doubleDistribution.median.toInt()

    override fun random(): Int {
        return doubleDistribution.random().toInt()
    }
}

val Distribution<Double>.asIntDistribution: Distribution<Int> get(){
    return WrappedIntDistribution(this)
}