package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.distributions.Distribution
import net.sourceforge.jdistlib.generic.GenericDistribution

data class ParametricDistribution(
        override val average: Int,
        override val minimum: Int,
        override val maximum: Int,
        override val mode: Int,
        override val median: Int,
        private val distribution: net.sourceforge.jdistlib.generic.GenericDistribution
) : com.hcsc.de.claims.distributions.Distribution<Int> {

    override fun random(): Int {
        return distribution.random().toInt()
    }
}