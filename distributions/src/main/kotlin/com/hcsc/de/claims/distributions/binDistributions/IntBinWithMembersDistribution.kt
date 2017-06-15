package com.hcsc.de.claims.distributions.binDistributions

import com.hcsc.de.claims.distributions.bins.BinWithMembers

data class IntBinWithMembersDistribution(
        override val bins: List<BinWithMembers<Int>>
) : BinWithMembersDistribution<Int, BinWithMembers<Int>>(
        rawBins = bins,
        toType = Double::toInt
)