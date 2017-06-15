package com.hcsc.de.claims.distributions.parametric

import com.hcsc.de.claims.math.helpers.ceilingOnEven

data class IntNormalDistribution(
        override val average: Int,
        override val minimum: Int,
        override val maximum: Int,
        override val mode: Int,
        override val median: Int,
        override val standardDeviation: Double
) : NormalDistribution<Int> {

    private val random = java.util.Random()

    override fun random(): Int {
        return (random.nextGaussian() * standardDeviation + average).ceilingOnEven().toInt()
    }
}