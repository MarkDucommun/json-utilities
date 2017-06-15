package com.hcsc.de.claims.distributions.parametric

import com.hcsc.de.claims.distributions.Distribution
import com.hcsc.de.claims.distributions.Randomable

data class IntParametricDistribution(
        override val average: Int,
        override val minimum: Int,
        override val maximum: Int,
        override val mode: Int,
        override val median: Int,
        private val distribution: Randomable<Int>
) : Distribution<Int>, Randomable<Int> by distribution