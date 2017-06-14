package com.hcsc.de.claims.distributions.parametric

data class NormalDoubleDistribution(
        override val average: Double,
        override val minimum: Double,
        override val maximum: Double,
        override val mode: Double,
        override val median: Double,
        override val standardDeviation: Double
) : NormalDistribution<Double> {

    private val random = java.util.Random()

    override fun random(): Double {
        return random.nextGaussian() * standardDeviation + average
    }
}