package com.hcsc.de.claims.distributions.parametric

data class ParametricIntDistribution(
        override val average: Int,
        override val minimum: Int,
        override val maximum: Int,
        override val mode: Int,
        override val median: Int,
        private val distribution: com.hcsc.de.claims.distributions.Randomable<Int>
) : com.hcsc.de.claims.distributions.Distribution<Int>, com.hcsc.de.claims.distributions.Randomable<Int> by distribution