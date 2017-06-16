package com.hcsc.de.claims.distributions.binDistributions

import com.hcsc.de.claims.distributions.bins.BinWithMembers

data class IntFixedWidthBinWithMembersDistribution(
        override val bins: List<BinWithMembers<Int>>,
        override val binWidth: Int
) : FixedWidthBinWithMembersDistribution<Int>(
        rawBins = bins,
        binWidth = binWidth,
        toType = Double::toInt
)