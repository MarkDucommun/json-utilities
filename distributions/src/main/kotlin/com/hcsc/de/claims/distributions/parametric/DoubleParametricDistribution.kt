package com.hcsc.de.claims.distributions.parametric

import com.hcsc.de.claims.distributions.Distribution
import com.hcsc.de.claims.distributions.Randomable

data class DoubleParametricDistribution(
        override val average: Double,
        override val minimum: Double,
        override val maximum: Double,
        override val mode: Double,
        override val median: Double,
        private val distribution: Randomable<Double>
) : Distribution<Double>, Randomable<Double> by distribution