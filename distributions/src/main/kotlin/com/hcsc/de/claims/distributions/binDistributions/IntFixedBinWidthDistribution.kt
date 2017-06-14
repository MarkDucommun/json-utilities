package com.hcsc.de.claims.distributions.binDistributions

import com.hcsc.de.claims.distributions.binDistributions.FixedBinWidthDistribution
import com.hcsc.de.claims.distributions.bins.Bin

data class IntFixedBinWidthDistribution(
        override val average: Int,
        override val minimum: Int,
        override val numberOfBins: Int,
        override val maximum: Int,
        override val sizeOfBin: Int,
        override val mode: Int,
        override val bins: List<Bin<Int>>,
        override val median: Int
) : FixedBinWidthDistribution<Int> {

    override fun random(): Int {
        TODO("not implemented")
    }
}