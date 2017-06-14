package com.hcsc.de.claims.distributions.parametric

data class ParametricDistribution(
        override val average: Double,
        override val minimum: Double,
        override val maximum: Double,
        override val mode: Double,
        override val median: Double,
        private val distribution: com.hcsc.de.claims.distributions.Randomable<Double>
) : com.hcsc.de.claims.distributions.Distribution<Double>, com.hcsc.de.claims.distributions.Randomable<Double> by distribution