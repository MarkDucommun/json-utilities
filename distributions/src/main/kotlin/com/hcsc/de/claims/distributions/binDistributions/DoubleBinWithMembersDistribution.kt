package com.hcsc.de.claims.distributions.binDistributions

import com.hcsc.de.claims.distributions.bins.BinWithMembers

data class DoubleBinWithMembersDistribution(
        override val bins: List<BinWithMembers<Double>>
) : BinWithMembersDistribution<Double, BinWithMembers<Double>>(
        rawBins = bins,
        toType = Double::toDouble
)