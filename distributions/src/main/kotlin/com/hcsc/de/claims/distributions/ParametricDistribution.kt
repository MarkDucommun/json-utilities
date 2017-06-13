package com.hcsc.de.claims.distributions

data class ParametricIntDistribution(
        override val average: Int,
        override val minimum: Int,
        override val maximum: Int,
        override val mode: Int,
        override val median: Int,
        private val distribution: Randomable<Int>
) : Distribution<Int>, Randomable<Int> by distribution

data class ParametricDistribution(
        override val average: Double,
        override val minimum: Double,
        override val maximum: Double,
        override val mode: Double,
        override val median: Double,
        private val distribution: Randomable<Double>
) : Distribution<Double>, Randomable<Double> by distribution